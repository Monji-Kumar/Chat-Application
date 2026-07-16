package com.monji.chatapp.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDirectChatRequest {
    private String targetAuthUserId;
    private String targetUsername;
    private String targetDisplayName;
    private String targetAvatarUrl;
}
