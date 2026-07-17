package com.monji.chatapp.chat_service.dto;

import com.monji.chatapp.chat_service.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSocketMessageRequest {
    private Long chatRoomId;
    private String content;
    private MessageType type;
}
