package com.monji.chatapp.user_service.exceptions;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class UserProfileNotFoundException extends AppException {

    public UserProfileNotFoundException(String message) {
        super(message, "USER_PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}