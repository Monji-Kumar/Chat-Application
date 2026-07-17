package com.monji.chatapp.chat_service.exception;

import com.monji.chatapp.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class ChatRoomNotFoundException extends AppException {

    public ChatRoomNotFoundException(String message) {
        super(message, "CHAT_ROOM_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
