package com.example.elice_3rd.chat.repository;

import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Flux<ChatMessage> findByChatRoomIdAndCreatedDateAfter(Long chatRoomId, LocalDateTime createdDate);

    Flux<ChatMessage> findByChatRoomId(Long chatRoomId);

    Mono<Void> deleteByChatRoomId(Long chatRoomId);
}
