package com.monji.chatapp.chat_service.dto;

import com.monji.chatapp.chat_service.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long chatRoomId;
    private String senderAuthUserId;
    private String senderUsername;
    private String content;
    private MessageType type;
    private Instant sentAt;
    private Instant editedAt;
    private Instant deletedAt;
}
