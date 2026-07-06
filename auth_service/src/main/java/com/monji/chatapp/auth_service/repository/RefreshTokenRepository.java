package com.monji.chatapp.auth_service.repository;

import com.monji.chatapp.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findByUserIdAndRevokedFalseOrderByCreatedAtAsc(Long userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
