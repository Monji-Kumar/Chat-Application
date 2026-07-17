package com.monji.chatapp.auth_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS",  HttpStatus.UNAUTHORIZED);
    }
}
