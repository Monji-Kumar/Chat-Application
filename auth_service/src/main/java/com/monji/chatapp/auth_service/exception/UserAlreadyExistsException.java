package com.monji.chatapp.auth_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}
