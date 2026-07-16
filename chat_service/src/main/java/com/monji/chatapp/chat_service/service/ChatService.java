package com.monji.chatapp.chat_service.service;

import com.monji.chatapp.chat_service.dto.*;

import java.util.List;

public interface ChatService {
    ChatRoomResponse createDirectChat(String authUserId, String username, CreateDirectChatRequest requestDto);

    ChatRoomResponse createGroupChat(String authUserId, String username, CreateGroupChatRequest requestDto);

    List<MessageResponse> getMessages(String authUserId, Long chatRoomId);

    MessageResponse sendMessage(String authUserId, String username, Long chatRoomId, SendMessageRequest requestDto);

    ChatRoomResponse getChatRoom(String authUserId, Long chatRoomId);

    List<ChatRoomResponse> getMyChats(String authUserId);
}
