package com.monji.chatapp.chat_service.entity;

import com.monji.chatapp.chat_service.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message", indexes = {
        @Index(name = "idx_message_id", columnList = "id"),
        @Index(name = "idx_message_chat_room_id", columnList = "chat_room_id"),
        @Index(name = "idx_message_sender_auth_user_id", columnList = "sender_auth_user_id"),
        @Index(name = "idx_message_seder_username", columnList = "sender_username")
})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_gen")
    @SequenceGenerator(name = "message_id_gen", sequenceName = "message_id_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "sender_auth_user_id",nullable = false)
    private String senderAuthUserId;

    @Column(name = "sender_username",nullable = false)
    private String senderUsername;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MessageType type;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
