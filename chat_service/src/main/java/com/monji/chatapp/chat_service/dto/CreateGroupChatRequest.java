package com.monji.chatapp.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatRequest {
    private String name;
    private String imageUrl;
    private List<CreateGroupMemberRequest> members;
}
