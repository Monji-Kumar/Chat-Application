package com.monji.chatapp.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile", indexes = {
        @Index(name = "idx_user_profile_id", columnList = "id"),
        @Index(name = "idx_user_profile_auth_user_id", columnList = "authUserId"),
        @Index(name = "idx_user_profile_username", columnList = "username"),
        @Index(name = "idx_user_profile_email", columnList = "email")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_id_gen")
    @SequenceGenerator(name = "user_profile_id_gen", sequenceName = "user_profile_id_seq",  allocationSize = 1,  initialValue = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String authUserId;
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String displayName;
    private String avatarUrl;

    @Column(unique = true, nullable = false)
    private String email;
    private String statusMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
