package com.monji.chatapp.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserProfileRequest {
    @NotBlank(message = "authUserId cannot be blank")
    private String authUserId;
    @NotBlank(message = "username cannot be blank")
    private String username;
    @NotBlank(message = "displayName cannot be blank")
    private String displayName;

    private String avatarUrl;

    @NotBlank(message = "email cannot be blank")
    @Email
    private String email;
    private String statusMessage;
}
