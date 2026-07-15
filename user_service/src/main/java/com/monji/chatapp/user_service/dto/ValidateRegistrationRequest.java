package com.monji.chatapp.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateRegistrationRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;
}
