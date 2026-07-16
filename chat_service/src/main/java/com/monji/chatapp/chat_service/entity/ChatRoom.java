package com.monji.chatapp.chat_service.entity;

import com.monji.chatapp.chat_service.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chat_room", indexes = {
        @Index(name = "idx_chat_room_id", columnList = "id"),
        @Index(name = "idx_chat_room_type", columnList = "type"),
        @Index(name = "idx_chat_room_created_by", columnList = "created_by")
})
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_id_gen")
    @SequenceGenerator(name = "chat_room_id_gen", sequenceName = "chat_room_id_seq",  allocationSize = 1, initialValue = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    @Column(nullable = false)
    private String name;
    private String imageUrl;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
