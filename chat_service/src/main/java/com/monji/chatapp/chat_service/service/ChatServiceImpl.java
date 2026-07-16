package com.monji.chatapp.chat_service.service;

import com.monji.chatapp.chat_service.dto.ChatRoomResponse;
import com.monji.chatapp.chat_service.dto.CreateDirectChatRequest;
import com.monji.chatapp.chat_service.dto.CreateGroupChatRequest;
import com.monji.chatapp.chat_service.dto.MessageResponse;
import com.monji.chatapp.chat_service.repository.ChatMemberRepository;
import com.monji.chatapp.chat_service.repository.ChatRoomRepository;
import com.monji.chatapp.chat_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChatRoomResponse createDirectChat(String authUserId, String username, CreateDirectChatRequest requestDto) {
        return null;
    }

    @Override
    public ChatRoomResponse createGroupChat(String authUserId, String username, CreateGroupChatRequest requestDto) {
        return null;
    }

    @Override
    public List<MessageResponse> getMessages(String authUserId, Long chatRoomId) {
        return List.of();
    }

    @Override
    public ChatRoomResponse sendMessage(String authUserId, String username, Long chatRoomId) {
        return null;
    }

    @Override
    public ChatRoomResponse getChatRoom(String authUserId, Long chatRoomId) {
        return null;
    }

    @Override
    public List<ChatRoomResponse> getMyChats(String authUserId) {
        return List.of();
    }
}
