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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (username == null || username.isBlank()) {
            throw new RuntimeException("Missing logged in Username");
        } else if (requestDto.getName() == null || requestDto.getName().isBlank()) {
            throw new RuntimeException("Missing Group chat name");
        } else if (requestDto.getMembers() == null || requestDto.getMembers().isEmpty()) {
            throw new RuntimeException("Missing group chat members");
        }

        //create new Chat Room
        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoomType.GROUP).name(requestDto.getName())
                .imageUrl(requestDto.getImageUrl())
                .createdBy(authUserId).build();

        chatRoom = chatRoomRepository.save(chatRoom);

        //create Owner
        ChatMember owner = ChatMember.builder().chatRoomId(chatRoom.getId())
                .authUserId(authUserId)
                .username(username)
                .displayName(username)
                .avatarUrl(null)
                .role(ChatMemberRole.OWNER)
                .joinedAt(Instant.now())
                .leftAt(null).build();

        owner = chatMemberRepository.save(owner);

        //create member users
        for(CreateGroupMemberRequest request : requestDto.getMembers()) {
            if(request.getAuthUserId() == null || request.getAuthUserId().isBlank()) {
                log.error("Chat Member does not have an Auth User ID");
            }
            if(authUserId.equals(request.getAuthUserId())) {
                continue;
            }

            ChatMember memberUser = ChatMember.builder().chatRoomId(chatRoom.getId())
                    .authUserId(request.getAuthUserId())
                    .username(request.getUsername())
                    .displayName(request.getDisplayName())
                    .avatarUrl(request.getAvatarUrl())
                    .role(ChatMemberRole.MEMBER)
                    .joinedAt(Instant.now())
                    .leftAt(null).build();

            chatMemberRepository.save(memberUser);

        }


        return mapChatRoom(chatRoom);
    }

    @Override
    public List<MessageResponse> getMessages(String authUserId, Long chatRoomId) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        }

        validateMember(chatRoomId, authUserId);

        return messageRepository.findAllByChatRoomIdAndDeletedAtIsNullOrderBySentAtAsc(chatRoomId)
                .stream().map(this::mapMessage).toList();
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
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        }

        validateMember(chatRoomId, authUserId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);

        return mapChatRoom(chatRoom);
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

    @Override
    @Transactional
    public ChatMemberResponse addMember(String authUserId, Long chatRoomId, AddChatMemberRequest requestDto) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        } else if (requestDto.getAuthUserId() == null || requestDto.getAuthUserId().isBlank()) {
            throw new RuntimeException("Missing New Member in User ID");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("Chat Room Not Found"));

        if(chatRoom.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Members can only be added to group chats");
        }

        ChatMember currentMember = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this chat"));

        if(currentMember.getRole() != ChatMemberRole.OWNER && currentMember.getRole() != ChatMemberRole.ADMIN) {
            throw new RuntimeException("Only Admins and Owners can add a new Member");
        }

        boolean isAlreadyMember = chatMemberRepository
                .existsByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId);

        if(isAlreadyMember) {
            throw new RuntimeException("User is already a member of this chat");
        }

        ChatMember member = ChatMember.builder()
                .chatRoomId(chatRoomId)
                .authUserId(requestDto.getAuthUserId())
                .username(requestDto.getUsername())
                .displayName(requestDto.getDisplayName())
                .avatarUrl(requestDto.getAvatarUrl())
                .role(ChatMemberRole.MEMBER)
                .joinedAt(Instant.now())
                .build();

        member = chatMemberRepository.save(member);

        return mapMember(member);
    }

    @Transactional
    @Override
    public ChatMemberResponse removeMember(String authUserId, Long chatRoomId, String memberAuthUserId) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        } else if (memberAuthUserId == null || memberAuthUserId.isBlank()) {
            throw new RuntimeException("Missing Member in User ID");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("Chat Room Not Found"));

        ChatMember currentMember = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this chat"));

        if(currentMember.getRole() != ChatMemberRole.OWNER && currentMember.getRole() != ChatMemberRole.ADMIN) {
            throw new RuntimeException("Only Admins and Owners can remove a new Member");
        }

        if(memberAuthUserId.equals(chatRoom.getCreatedBy())) {
            throw new RuntimeException("Group owner cannot be removed");
        }

        ChatMember memberToRemove = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, memberAuthUserId)
                .orElseThrow(() -> new RuntimeException("User is not an active member of this chat"));

        memberToRemove.setLeftAt(Instant.now());

        memberToRemove = chatMemberRepository.save(memberToRemove);

        return mapMember(memberToRemove);
    }

    @Transactional
    @Override
    public ChatMemberResponse leaveChatRoom(String authUserId, Long chatRoomId) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (chatRoom.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Only group chats can be left");
        }

        if (authUserId.equals(chatRoom.getCreatedBy())) {
            throw new RuntimeException("Group owner cannot leave before transferring ownership");
        }

        ChatMember member = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId)
                .orElseThrow(() -> new RuntimeException("You are not an active member of this chat"));

        member.setLeftAt(Instant.now());

        member = chatMemberRepository.save(member);

        return mapMember(member);
    }

    @Override
    @Transactional
    public ChatMemberResponse updateMemberRole(String authUserId, Long chatRoomId, String memberAuthUserId, UpdateMemberRoleRequest requestDto) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        } else if (memberAuthUserId == null || memberAuthUserId.isBlank()) {
            throw new RuntimeException("Missing Member in User ID");
        }

        if(requestDto.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if(requestDto.getRole() == ChatMemberRole.OWNER) {
            throw new RuntimeException("Use transfer ownership API to make someone owner");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (chatRoom.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Roles can only be changed in group chats");
        }

        ChatMember currentMember = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId)
                .orElseThrow(() -> new RuntimeException("You are not a member of this chat"));

        if (currentMember.getRole() != ChatMemberRole.OWNER) {
            throw new RuntimeException("Only owner can update member roles");
        }

        if (authUserId.equals(memberAuthUserId)) {
            throw new RuntimeException("Owner cannot change their own role");
        }

        ChatMember targetMember = chatMemberRepository
                .findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, memberAuthUserId)
                .orElseThrow(() -> new RuntimeException("Target user is not an active member"));

        targetMember.setRole(requestDto.getRole());

        targetMember = chatMemberRepository.save(targetMember);

        return mapMember(targetMember);
    }

    @Override
    @Transactional
    public ChatRoomResponse updateChatRoomOwnership(String authUserId, Long chatRoomId, String memberAuthUserId) {
        if(authUserId == null || authUserId.isBlank()) {
            throw new RuntimeException("Missing logged in User ID");
        } else if (chatRoomId == null || chatRoomId <= 0) {
            throw new RuntimeException("Missing logged in Chat Room ID");
        } else if (memberAuthUserId == null || memberAuthUserId.isBlank()) {
            throw new RuntimeException("Missing Member in User ID");
        }

        if(authUserId.equals(memberAuthUserId)) {
            throw new RuntimeException("You are already the owner");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("No Chat Room found"));

        if(chatRoom.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Ownership can only be transferred for group chats");
        }

        ChatMember currentMember = chatMemberRepository.findByChatRoomIdAndAuthUserIdAndLeftAtIsNull(chatRoomId, authUserId).orElseThrow(() -> new RuntimeException("You are not a member of this Chat"));

        if(currentMember.getRole() != ChatMemberRole.OWNER) {
            throw new RuntimeException("Only owner can transfer ownership");
        }

        ChatMember newOwner = chatMemberRepository.findByChatRoomIdAndAuthUserIdAndLeftAtIsNull
                        (chatRoomId, memberAuthUserId)
                .orElseThrow(() -> new RuntimeException("New owner must be an active member"));

        currentMember.setRole(ChatMemberRole.MEMBER);
        newOwner.setRole(ChatMemberRole.OWNER);

        chatRoom.setCreatedBy(newOwner.getAuthUserId());

        chatMemberRepository.save(currentMember);
        chatMemberRepository.save(newOwner);
        chatRoom = chatRoomRepository.save(chatRoom);

        return mapChatRoom(chatRoom);

    }

}
