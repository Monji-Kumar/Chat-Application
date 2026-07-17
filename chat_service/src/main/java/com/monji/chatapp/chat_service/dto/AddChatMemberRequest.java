package com.monji.chatapp.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddChatMemberRequest {
    private String authUserId;
    private String username;
    private String displayName;
    private String avatarUrl;
}
