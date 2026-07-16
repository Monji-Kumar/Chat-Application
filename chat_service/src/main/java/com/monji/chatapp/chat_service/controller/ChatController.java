package com.monji.chatapp.chat_service.controller;

import com.monji.chatapp.chat_service.dto.*;
import com.monji.chatapp.chat_service.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(path = "/direct")
    public ResponseEntity<?> createDirectChat(@RequestBody CreateDirectChatRequest requestDto,
                                                   HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        ChatRoomResponse responseDto = chatService.createDirectChat(authUserId, username, requestDto);
        return ResponseEntity.ok(responseDto);

    }

    @PostMapping(path = "/group")
    public ResponseEntity<?> createGroupChat(@RequestBody CreateGroupChatRequest requestDto, HttpServletRequest request) {

        String authUserId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        ChatRoomResponse responseDto = chatService.createGroupChat(authUserId, username, requestDto);
        return ResponseEntity.ok(responseDto);

    }

    @GetMapping
    public ResponseEntity<?> getMyChats(HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        List<ChatRoomResponse> responseDto = chatService.getMyChats(authUserId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(path = "/by-room-id")
    public ResponseEntity<?> getChatRoom(@RequestParam Long chatRoomId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatRoomResponse responseDto = chatService.getChatRoom(authUserId, chatRoomId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(path = "/by-room-id/messages")
    public ResponseEntity<?> sendMessage(@RequestParam Long chatRoomId, @RequestBody SendMessageRequest requestDto, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        MessageResponse responseDto = chatService.sendMessage(authUserId, username, chatRoomId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(path = "/by-room-id/messages")
    public ResponseEntity<?> getMessages(@RequestParam Long chatRoomId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        List<MessageResponse> response = chatService.getMessages(authUserId, chatRoomId);
        return ResponseEntity.ok(response);
    }
}
