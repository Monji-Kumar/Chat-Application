package com.monji.chatapp.chat_service.dto;

import com.monji.chatapp.chat_service.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageRequest {
    private String content;
    private MessageType messageType;
}
