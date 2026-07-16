package com.monji.chatapp.chat_service.service;

import com.monji.chatapp.chat_service.dto.*;
import com.monji.chatapp.chat_service.entity.ChatMember;
import com.monji.chatapp.chat_service.entity.ChatRoom;
import com.monji.chatapp.chat_service.entity.Message;
import com.monji.chatapp.chat_service.enums.ChatMemberRole;
import com.monji.chatapp.chat_service.enums.ChatRoomType;
import com.monji.chatapp.chat_service.enums.MessageType;
import com.monji.chatapp.chat_service.repository.ChatMemberRepository;
import com.monji.chatapp.chat_service.repository.ChatRoomRepository;
import com.monji.chatapp.chat_service.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ChatRoomResponse createDirectChat(String authUserId, String username, CreateDirectChatRequest requestDto) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (username == null || username.isBlank()) {
            throw new RuntimeException("Missing logged in Username");
        }

        if(requestDto.getTargetAuthUserId() == null || requestDto.getTargetAuthUserId().isBlank()) {
            throw new RuntimeException("Target User id is required");
        }
        //TODO: this is to be removed later on
        else if (Objects.equals(authUserId, requestDto.getTargetAuthUserId())) {
            throw new RuntimeException("Cannot send message to yourself");
        }
        Optional<ChatRoom> chatRoomOpt = findExistingDirectChat(authUserId, requestDto.getTargetAuthUserId());

        if(chatRoomOpt.isPresent()) {
            return mapChatRoom(chatRoomOpt.get());
        }

        //Create new Room
        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoomType.DIRECT).name(username+"-"+requestDto.getTargetUsername())
                .createdBy(authUserId).build();

        chatRoom = chatRoomRepository.save(chatRoom);

        //Create Chat Room Members
        ChatMember currentChatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getId())
                .authUserId(authUserId)
                .username(username)
                .displayName(username)
                .role(ChatMemberRole.MEMBER)
                .joinedAt(Instant.now())
                .build();

        ChatMember targetChatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getId())
                .authUserId(requestDto.getTargetAuthUserId())
                .username(requestDto.getTargetUsername())
                .displayName(requestDto.getTargetDisplayName())
                .avatarUrl(requestDto.getTargetAvatarUrl())
                .role(ChatMemberRole.MEMBER)
                .joinedAt(Instant.now())
                .build();

        chatMemberRepository.save(currentChatMember);
        chatMemberRepository.save(targetChatMember);

        return mapChatRoom(chatRoom);
    }

    private Optional<ChatRoom> findExistingDirectChat(String authUserId, String targetAuthUserId) {
        List<ChatMember> myMemberships = chatMemberRepository.findByAuthUserIdAndLeftAtIsNull(authUserId);

        for(ChatMember member : myMemberships) {
            Long chatRoomId = member.getChatRoomId();

            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(chatRoomId);

            if(chatRoomOpt.isEmpty()) {
                continue;
            }

            ChatRoom chatRoom = chatRoomOpt.get();
            if(chatRoom.getType() != ChatRoomType.DIRECT) {
                continue;
            }

            boolean targetIsMember = chatMemberRepository.existsByChatRoomIdAndAuthUserId(chatRoomId, targetAuthUserId);

            if(targetIsMember) {
                return Optional.of(chatRoom);
            }
        }

        return Optional.empty();
    }

    @Override
    public ChatRoomResponse createGroupChat(String authUserId, String username, CreateGroupChatRequest requestDto) {
        return null;
    }

    @Override
    public List<MessageResponse> getMessages(String authUserId, Long chatRoomId) {
        return new ArrayList<>();
    }

    @Override
    public MessageResponse sendMessage(String authUserId, String username, Long chatRoomId, SendMessageRequest requestDto) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if(username == null || username.isBlank()) {
            throw new RuntimeException("Missing logged in Username");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing chat room id");
        } else if (requestDto.getContent() == null || requestDto.getContent().isBlank()) {
            throw new RuntimeException("Missing message content");
        }

        validateMember(chatRoomId, authUserId);

        Message message = Message.builder().chatRoomId(chatRoomId).senderAuthUserId(authUserId)
                .senderUsername(username).content(requestDto.getContent())
                .type(requestDto.getMessageType() == null ? MessageType.TEXT :
                        requestDto.getMessageType())
                .sentAt(Instant.now()).build();

        message = messageRepository.save(message);

        return mapMessage(message);

    }

    @Override
    public ChatRoomResponse getChatRoom(String authUserId, Long chatRoomId) {
        return null;
    }

    @Override
    public List<ChatRoomResponse> getMyChats(String authUserId) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        }

        List<ChatMember> memberships = chatMemberRepository.findByAuthUserIdAndLeftAtIsNull(authUserId);

        return memberships.stream()
                .map(ChatMember::getChatRoomId)
                .distinct()
                .map(chatRoomRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapChatRoom)
                .toList();
    }

    private void validateMember(Long chatRoomId, String authUserId) {
        boolean isMember = chatMemberRepository.existsByChatRoomIdAndAuthUserId(chatRoomId, authUserId);

        if(!isMember){
            throw new RuntimeException("You are not a member of this chat");
        }
    }

    private MessageResponse mapMessage(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderAuthUserId(message.getSenderAuthUserId())
                .senderUsername(message.getSenderUsername())
                .content(message.getContent())
                .type(message.getType())
                .sentAt(message.getSentAt())
                .editedAt(message.getEditedAt())
                .deletedAt(message.getDeletedAt())
                .build();
    }

    private ChatMemberResponse mapMember(ChatMember chatMember) {
        return ChatMemberResponse.builder()
                .id(chatMember.getId())
                .chatRoomId(chatMember.getChatRoomId())
                .authUserId(chatMember.getAuthUserId())
                .username(chatMember.getUsername())
                .displayName(chatMember.getDisplayName())
                .avatarUrl(chatMember.getAvatarUrl())
                .role(chatMember.getRole())
                .joinedAt(chatMember.getJoinedAt())
                .leftAt(chatMember.getLeftAt())
                .build();
    }

    private ChatRoomResponse mapChatRoom(ChatRoom chatRoom) {

        List<ChatMemberResponse> members = chatMemberRepository
                .findAllByChatRoomIdAndLeftAtIsNull(chatRoom.getId()).stream()
                .map(this::mapMember).toList();

        MessageResponse lastMessage = messageRepository
                .findFirstByChatRoomIdAndDeletedAtIsNullOrderBySentAtDesc(chatRoom.getId())
                .map(this::mapMessage)
                .orElse(null);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .type(chatRoom.getType())
                .name(chatRoom.getName())
                .imageUrl(chatRoom.getImageUrl())
                .createdBy(chatRoom.getCreatedBy())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .members(members)
                .lastMessage(lastMessage).build();
    }
}
