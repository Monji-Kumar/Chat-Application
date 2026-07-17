package com.monji.chatapp.chat_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class ChatAccessDeniedException extends AppException {

    public ChatAccessDeniedException(String message) {
        super(message, "CHAT_ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }
}
