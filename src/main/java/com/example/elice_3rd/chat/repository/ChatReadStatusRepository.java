package com.example.elice_3rd.chat.repository;

import com.example.elice_3rd.chat.entity.mysql.ChatReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {

    Optional<ChatReadStatus> findByChatMessageIdAndReceiver_memberId(String chatMessageId, Long receiverId);

    List<ChatReadStatus> findByChatMessageId(String chatMessageId);

    void deleteByChatMessageId(String chatMessageId);
}
