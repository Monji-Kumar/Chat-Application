package com.monji.chatapp.chat_service.controller;

import com.monji.chatapp.chat_service.dto.*;
import com.monji.chatapp.chat_service.service.ChatService;
import com.monji.chatapp.common.response.ApiResponse;
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
        return ResponseEntity.ok(ApiResponse.success("Direct Chat room created successfully", responseDto));

    }

    @PostMapping(path = "/group")
    public ResponseEntity<?> createGroupChat(@RequestBody CreateGroupChatRequest requestDto, HttpServletRequest request) {

        String authUserId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        ChatRoomResponse responseDto = chatService.createGroupChat(authUserId, username, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Group Chat room created successfully", responseDto));

    }

    @GetMapping
    public ResponseEntity<?> getMyChats(HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        List<ChatRoomResponse> responseDto = chatService.getMyChats(authUserId);
        return ResponseEntity.ok(ApiResponse.success("Chats retrieved successfully", responseDto));
    }

    @GetMapping(path = "/by-room-id")
    public ResponseEntity<?> getChatRoom(@RequestParam Long chatRoomId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatRoomResponse responseDto = chatService.getChatRoom(authUserId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.success("Chat room retrieved successfully", responseDto));
    }

    @PostMapping(path = "/by-room-id/messages")
    public ResponseEntity<?> sendMessage(@RequestParam Long chatRoomId, @RequestBody SendMessageRequest requestDto, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-User-Name");

        MessageResponse responseDto = chatService.sendMessage(authUserId, username, chatRoomId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", responseDto));
    }

    @GetMapping(path = "/by-room-id/messages")
    public ResponseEntity<?> getMessages(@RequestParam Long chatRoomId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        List<MessageResponse> response = chatService.getMessages(authUserId, chatRoomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/by-room-id/group/add-members")
    public ResponseEntity<?> addGroupChatMember(@RequestParam Long chatRoomId , @RequestBody AddChatMemberRequest requestDto, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatMemberResponse responseDto = chatService.addMember(authUserId, chatRoomId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("New member added successfully", responseDto));
    }

    @DeleteMapping(path = "/by-room-id/group/remove")
    public ResponseEntity<?> removeMember(@RequestParam Long chatRoomId, String memberAuthUserId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatMemberResponse responseDto = chatService.removeMember(authUserId, chatRoomId, memberAuthUserId);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", responseDto));
    }

    @PostMapping(path = "/by-room-id/group/leave")
    public ResponseEntity<?> leaveGroup(@RequestParam Long chatRoomId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatMemberResponse responseDto = chatService.leaveChatRoom(authUserId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.success("Chat room left successfully", responseDto));
    }

    @PatchMapping(path = "/by-room-id/group/role")
    public ResponseEntity<?> changeMemberRole(@RequestParam Long chatRoomId, @RequestParam String memberAuthUserId, @RequestBody UpdateMemberRoleRequest requestDto, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatMemberResponse responseDto = chatService.updateMemberRole(authUserId, chatRoomId, memberAuthUserId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Chat room member role updated successfully", responseDto));
    }

    @PatchMapping(path = "/by-room-id/group/ownership")
    public ResponseEntity<?> changeChatRoomOnwership(@RequestParam Long chatRoomId, @RequestParam String memberAuthUserId, HttpServletRequest request) {
        String authUserId = request.getHeader("X-User-Id");

        ChatRoomResponse responseDto = chatService.updateChatRoomOwnership(authUserId, chatRoomId, memberAuthUserId);
        return ResponseEntity.ok(ApiResponse.success("Chat room ownership changed successfully", responseDto));
    }
}
