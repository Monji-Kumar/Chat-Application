package com.monji.chatapp.chat_service.handshake;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Component
@Slf4j
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake-BEGIN");
        String authUserId = request.getHeaders().getFirst("X-User-Id");
        String username = request.getHeaders().getFirst("X-User-Name");
        if(authUserId == null || authUserId.isBlank() || username==null || username.isBlank()) {
            return false;
        }

        attributes.put("username", username);
        attributes.put("authUserId", authUserId);

        log.info("beforeHandshake-END");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
        log.info("afterHandshake");
    }
}
