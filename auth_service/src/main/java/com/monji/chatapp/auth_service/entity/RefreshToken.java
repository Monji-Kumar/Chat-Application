package com.monji.chatapp.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_refresh_token_id", columnList = "id"),
        @Index(name = "idx_refresh_token_user_id", columnList = "userId")
})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_id_seq_gen")
    @SequenceGenerator(sequenceName = "refresh_id_seq", name = "refresh_id_seq_gen", initialValue =  1, allocationSize = 1)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "is_revoked")
    private boolean revoked;

    @Column(name = "created_at")
    private Instant createdAt;
}
