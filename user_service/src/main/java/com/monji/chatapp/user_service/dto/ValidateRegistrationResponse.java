package com.monji.chatapp.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateRegistrationResponse {
    private boolean valid;
    private boolean usernameAvailable;
    private boolean emailAvailable;
    private String message;
}
