package com.monji.chatapp.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final Long refreshTokenExpiration;
    private final Long accessTokenExpiration;

    public JwtService(@Value("${app.jwt.secret}") String secretKey,
                      @Value("${app.jwt.access-expiration-ms}")Long accessTokenExpiration,
                      @Value("${app.jwt.refresh-expiration-ms}")Long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Long userId, String username, String role) {
        return buildToken(userId, username, accessTokenExpiration, "access", role);
    }

    public String generateRefreshToken(Long userId, String username, String role) {
        return buildToken(userId, username, refreshTokenExpiration, "refresh", role);
    }

    public String buildToken(Long userId, String username, Long expiration, String type, String role) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", type)
                .claim("expiration", expirationDate)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(parseAndValidate(token).getSubject());
    }

    public String extractUsername(String token) {
        return parseAndValidate(token).get("username").toString();
    }

    public String extractRole(String token) {
        return parseAndValidate(token).get("role").toString();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseAndValidate(token).get("type", String.class));
    }
}

