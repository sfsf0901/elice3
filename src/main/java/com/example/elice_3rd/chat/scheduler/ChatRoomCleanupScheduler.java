package com.example.elice_3rd.chat.scheduler;

import com.example.elice_3rd.chat.dto.ChatRoomDeletedEvent;
import com.example.elice_3rd.chat.entity.ChatMessage;
import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import com.example.elice_3rd.chat.repository.ChatReadStatusRepository;
import com.example.elice_3rd.chat.repository.ChatRoomRepository;
import com.example.elice_3rd.chat.repository.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCleanupScheduler {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 4 ? * TUE") // 매주 화요일 새벽 4시에 실행
    public void cleanupInactiveChatRooms() {
        // INACTIVE 상태인 채팅방들을 조회
        List<ChatRoom> inactiveChatRooms = chatRoomRepository.findByRoomStatus(RoomStatus.INACTIVE);

        for (ChatRoom chatRoom : inactiveChatRooms) {
            Long chatRoomId = chatRoom.getChatRoomId();

            // 해당 채팅방에 속한 메시지들 조회
            Flux<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId);

            chatMessages
                .collectList()
                .flatMap(messages -> {
                    log.debug("Deleting {} messages for chat room {}", messages.size(), chatRoomId);
                    // 각 메시지에 대해 chatReadStatus 삭제 (비동기 처리)
                    return deleteChatReadStatusForMessages(messages);
                })
                .flatMap(v -> chatMessageRepository.deleteByChatRoomId(chatRoomId))
                .doOnSuccess(v -> // MongoDB 삭제 후 JPA 삭제 실행 (블로킹-safe)
                    eventPublisher.publishEvent(new ChatRoomDeletedEvent(this, chatRoomId))
                )
                .subscribe(); // 리액티브 체인 실행
        }
    }

    private Mono<Void> deleteChatReadStatusForMessages(List<ChatMessage> messages) {
        return Flux.fromIterable(messages)
                .flatMap(chatMessage -> Mono.fromRunnable(() -> {
                    chatReadStatusRepository.deleteByChatMessageId(chatMessage.getChatMessageId());
                    log.debug("Deleted read status for chat message {}", chatMessage.getChatMessageId());
                }).subscribeOn(Schedulers.boundedElastic())) // JPA 작업을 별도의 스레드에서 처리
                .then(); // 모든 작업이 완료될 때까지 기다림
    }

    // JPA 관련 삭제는 블로킹이므로 별도 스레드에서 실행
    @EventListener
    @Transactional
    public void handleChatRoomDeletion(ChatRoomDeletedEvent event) {
        Long chatRoomId = event.getChatRoomId();

        log.debug("Deleting member statuses for chat room {}", chatRoomId);
        memberStatusRepository.deleteByChatRoom_ChatRoomId(chatRoomId);

        log.debug("Deleting chat room {}", chatRoomId);
        chatRoomRepository.deleteById(chatRoomId);
    }
}
