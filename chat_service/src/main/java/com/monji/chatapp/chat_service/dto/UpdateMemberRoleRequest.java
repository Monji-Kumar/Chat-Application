package com.monji.chatapp.chat_service.dto;

import lombok.*;

import com.monji.chatapp.chat_service.enums.ChatMemberRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRoleRequest {
    private ChatMemberRole role;
}
