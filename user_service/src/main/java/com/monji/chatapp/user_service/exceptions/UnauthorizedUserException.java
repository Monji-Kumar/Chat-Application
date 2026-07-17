package com.monji.chatapp.user_service.exceptions;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends AppException {

    public UnauthorizedUserException(String message) {
        super(message, "UNAUTHORIZED_USER", HttpStatus.UNAUTHORIZED);
    }
}
