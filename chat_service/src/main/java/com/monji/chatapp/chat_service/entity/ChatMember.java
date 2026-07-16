package com.monji.chatapp.chat_service.entity;

import com.monji.chatapp.chat_service.enums.ChatMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "chat_member", indexes = {
        @Index(name = "idx_chat_member_id", columnList = "id"),
        @Index(name = "idx_chat_member_chat_room_id", columnList = "chat_room_id"),
        @Index(name = "idx_chat_member_auth_user_id", columnList = "auth_user_id"),
        @Index(name = "idx_chat_member_username", columnList = "username")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"chat_room_id", "auth_user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMember {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_member_id_gen")
    @SequenceGenerator(name = "chat_member_id_gen",sequenceName = "chat_member_id_seq", allocationSize=1, initialValue=1)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "auth_user_id", nullable = false)
    private String authUserId;

    @Column(nullable = false)
    private String username;

    private String displayName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMemberRole role;

    @Column(nullable = false)
    private Instant joinedAt;

    private Instant leftAt;
}
