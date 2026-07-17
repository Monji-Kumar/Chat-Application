package com.monji.chatapp.chat_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class InvalidChatRequestException extends AppException {

    public InvalidChatRequestException(String message) {
        super(message, "INVALID_CHAT_REQUEST", HttpStatus.BAD_REQUEST);
    }
}