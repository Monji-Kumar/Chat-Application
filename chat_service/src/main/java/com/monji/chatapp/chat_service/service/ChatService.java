package com.monji.chatapp.chat_service.service;

import com.monji.chatapp.chat_service.dto.ChatRoomResponse;
import com.monji.chatapp.chat_service.dto.CreateDirectChatRequest;
import com.monji.chatapp.chat_service.dto.CreateGroupChatRequest;
import com.monji.chatapp.chat_service.dto.MessageResponse;

import java.util.List;

public interface ChatService {
    ChatRoomResponse createDirectChat(String authUserId, String username, CreateDirectChatRequest requestDto);

    ChatRoomResponse createGroupChat(String authUserId, String username, CreateGroupChatRequest requestDto);

    List<MessageResponse> getMessages(String authUserId, Long chatRoomId);

    ChatRoomResponse sendMessage(String authUserId, String username, Long chatRoomId);

    ChatRoomResponse getChatRoom(String authUserId, Long chatRoomId);

    List<ChatRoomResponse> getMyChats(String authUserId);
}
