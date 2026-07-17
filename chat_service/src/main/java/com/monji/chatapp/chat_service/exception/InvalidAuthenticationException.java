package com.monji.chatapp.chat_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class InvalidAuthenticationException extends AppException {
    public InvalidAuthenticationException(String message) {
        super(message, "MISSING_AUTH_DETAILS", HttpStatus.FORBIDDEN);
    }
}
