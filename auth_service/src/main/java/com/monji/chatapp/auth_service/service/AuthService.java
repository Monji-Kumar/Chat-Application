package com.monji.chatapp.auth_service.service;

import com.monji.chatapp.auth_service.dto.LoginRequestDto;
import com.monji.chatapp.auth_service.dto.RegisterRequestDto;
import com.monji.chatapp.auth_service.entity.RefreshToken;
import com.monji.chatapp.auth_service.entity.User;
import com.monji.chatapp.auth_service.exception.InvalidCredentialsException;
import com.monji.chatapp.auth_service.exception.UserAlreadyExistsException;
import com.monji.chatapp.auth_service.repository.RefreshTokenRepository;
import com.monji.chatapp.auth_service.repository.UserRepository;
import com.monji.chatapp.common.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Value("${app.auth.max-active-sessions}")
    private int maxActiveSessions;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    public void registerUser(RegisterRequestDto registerRequestDto) {
        String username = registerRequestDto.getUsername();
        String password = registerRequestDto.getPassword();
        String email = registerRequestDto.getEmail();
        String phone = registerRequestDto.getPhone();
        String name = registerRequestDto.getName();

        Optional<User> userOpt = userRepository.findByEmailOrPhoneOrUsername(email, phone, username);
       if(userOpt.isPresent()){
           throw new UserAlreadyExistsException("User already exists");
       }

       String hashedPassword = passwordEncoder.encode(password);

       User user = User.builder()
               .username(username)
               .password(hashedPassword)
               .email(email)
               .phone(phone)
               .name(name)
               .build();

       userRepository.save(user);
    }

    public void loginUser(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        String  username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(!userOpt.isPresent()){
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid username or password");
        }

        setAuthCookies(user, request, response);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        } else {
            throw new InvalidCredentialsException("Invalid request");
        }

        if (refreshToken == null) {
            throw new InvalidCredentialsException("No refresh token cookie present");
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Provided token is not a refresh token");
        }

        String hashedToken = hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashedToken).orElseThrow(
                () -> new InvalidCredentialsException("Refresh token not recognized")
        );

        if(storedToken.isRevoked()) {
            throw new InvalidCredentialsException("Refresh Token has been revoked");
        }

        if(storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException("Refresh Token has expired");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        Long userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidCredentialsException("No User found with the given Id"));

        setAuthCookies(user, null, response);
    }

    private void setAuthCookies(User user, HttpServletRequest request, HttpServletResponse response) {

        List<RefreshToken> activeSessions = refreshTokenRepository.findByUserIdAndRevokedFalseOrderByCreatedAtAsc(user.getId());

        while(activeSessions.size() >= maxActiveSessions){
            RefreshToken refreshToken = activeSessions.removeFirst();
            refreshTokenRepository.delete(refreshToken);
        }

        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername(), user.getRole().name());
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());

        Instant expiresAt = jwtService.parseAndValidate(refreshToken).getExpiration().toInstant();
        RefreshToken tokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(refreshToken))
                .expiresAt(expiresAt)
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(tokenEntity);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            String tokenHash = hashToken(refreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(storedToken -> {
                storedToken.setRevoked(true);
                refreshTokenRepository.save(storedToken);
            });
        }

        ResponseCookie expiredAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build();
        ResponseCookie expiredRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build();

        response.addHeader("Set-Cookie", expiredAccessCookie.toString());
        response.addHeader("Set-Cookie", expiredRefreshCookie.toString());
    }
}
