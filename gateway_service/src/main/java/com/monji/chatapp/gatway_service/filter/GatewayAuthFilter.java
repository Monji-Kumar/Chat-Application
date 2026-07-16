package com.monji.chatapp.gatway_service.filter;

import com.monji.chatapp.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

public class GatewayAuthFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout"
    );

    public static HandlerFilterFunction<ServerResponse, ServerResponse> authenticate() {
        return ((request, next) -> {
            String path = request.path();

            if (isBlockedInternalPath(path)) {
                return ServerResponse.status(HttpStatus.FORBIDDEN)
                        .body("Forbidden");
            }

            if(isPublicPath(path)) {
                return next.handle(request);
            }

            String authHeader = request.headers().firstHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User");
            }

            JwtService jwtService = MvcUtils.getApplicationContext(request).getBean(JwtService.class);

            String token =  authHeader.replace("Bearer ", "");

            if(!jwtService.isValidToken(token)) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User");
            }

            Long userId = jwtService.extractUserId(token);
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Name", username)
                    .header("X-User-Role", role)
                    .build();

            return next.handle(modifiedRequest);
        });
    }

    private static boolean isBlockedInternalPath(String path) {
        return path.startsWith("/api/users/internal");
    }

    private static boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::equals);
    }
}
