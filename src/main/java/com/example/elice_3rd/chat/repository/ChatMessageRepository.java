package com.example.elice_3rd.chat.repository;

import com.example.elice_3rd.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    @Tailable
    Flux<ChatMessage> findByChatRoomIdAndCreatedDateAfter(Long chatRoomId, LocalDateTime createdDate);

    void deleteByChatRoomId(Long chatRoomId);
}
