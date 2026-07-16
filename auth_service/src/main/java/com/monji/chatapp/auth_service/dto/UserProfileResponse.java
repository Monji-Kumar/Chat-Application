package com.monji.chatapp.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String authUserId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String email;
    private String statusMessage;
}
