package com.monji.chatapp.chat_service.controller;

import com.monji.chatapp.chat_service.dto.ChatSocketMessageRequest;
import com.monji.chatapp.chat_service.dto.MessageResponse;
import com.monji.chatapp.chat_service.dto.SendMessageRequest;
import com.monji.chatapp.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatSocketMessageRequest request, Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        String authUserId = principal.getName();
        String username = headerAccessor.getSessionAttributes().get("username").toString();

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setContent(request.getContent());
        sendMessageRequest.setMessageType(request.getType());

        MessageResponse savedMessage = chatService.sendMessage(
                authUserId,
                authUserId,
                request.getChatRoomId(),
                sendMessageRequest
        );

        messagingTemplate.convertAndSend(
                "/topic/chats/" + request.getChatRoomId(),
                savedMessage
        );
    }
}
