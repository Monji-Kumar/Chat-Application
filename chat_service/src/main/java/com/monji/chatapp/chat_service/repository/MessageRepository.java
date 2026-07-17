package com.monji.chatapp.chat_service.repository;

import com.monji.chatapp.chat_service.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findFirstByChatRoomIdAndDeletedAtIsNullOrderBySentAtDesc(Long chatRoomId);

    List<Message> findAllByChatRoomIdAndDeletedAtIsNullOrderBySentAtAsc(Long chatRoomId);
}
