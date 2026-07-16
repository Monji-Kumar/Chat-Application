package com.monji.chatapp.chat_service.dto;

import com.monji.chatapp.chat_service.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private ChatRoomType type;
    private String name;
    private String imageUrl;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ChatMemberResponse> members;
    private MessageResponse lastMessage;
}
