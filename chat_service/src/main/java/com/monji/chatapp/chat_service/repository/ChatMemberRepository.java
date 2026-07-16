package com.monji.chatapp.chat_service.repository;

import com.monji.chatapp.chat_service.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    boolean existsByChatRoomIdAndAuthUserId(Long chatRoomId, String authUserId);

    List<ChatMember> findAllByChatRoomIdAndLeftAtIsNull(Long chatRoomId);

    List<ChatMember> findByAuthUserIdAndLeftAtIsNull(String authUserId);
}
