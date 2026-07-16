package com.monji.chatapp.chat_service.dto;

import com.monji.chatapp.chat_service.enums.ChatMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMemberResponse {
    private Long id;
    private Long chatRoomId;
    private String authUserId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private ChatMemberRole role;
    private Instant joinedAt;
    private Instant leftAt;
}
