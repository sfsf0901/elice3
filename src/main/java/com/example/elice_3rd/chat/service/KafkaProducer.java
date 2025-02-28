package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ChatMessage> chatMessageKafkaTemplate;
    private final KafkaTemplate<String, NotificationDto> notificationKafkaTemplate;

    @Value("${spring.kafka.chatTopic}")
    private String chatTopic;

    @Value("${spring.kafka.notificationTopic}")
    private String notificationTopic;

    public CompletableFuture<Void> sendMessage(ChatMessage chatMessage) {
        return sendMessageWithRetry(chatMessage, 3, 1000L); // 최대 3회 재시도, 1초 간격
    }

    public CompletableFuture<Void> sendMessage(NotificationDto notificationDto) {
        return sendMessageWithRetry(notificationDto, 3, 1000L); // 최대 3회 재시도, 1초 간격
    }

    private CompletableFuture<Void> sendMessageWithRetry(ChatMessage chatMessage, int maxRetryAttempts, long retryDelay) {
        return sendMessageAsync(chatMessage, 0, maxRetryAttempts, retryDelay);
    }

    private CompletableFuture<Void> sendMessageWithRetry(NotificationDto notificationDto, int maxRetryAttempts, long retryDelay) {
        return sendMessageAsync(notificationDto, 0, maxRetryAttempts, retryDelay);
    }

    private CompletableFuture<Void> sendMessageAsync(ChatMessage chatMessage, int attempt, int maxRetryAttempts, long retryDelay) {
        // Kafka로 메시지 전송
        CompletableFuture<SendResult<String, ChatMessage>> sendResultFuture = chatMessageKafkaTemplate.send(chatTopic, chatMessage);

        // 전송 결과 처리
        return sendResultFuture.handleAsync((result, e) -> {
            if (e != null) {
                log.error("Attempt {} failed to send message: {}", attempt + 1, chatMessage, e);

                if (attempt + 1 < maxRetryAttempts) {
                    log.warn("Retrying in {} ms...", retryDelay);
                    // 재시도
                    CompletableFuture<Void> retryFuture = new CompletableFuture<>();
                    CompletableFuture.delayedExecutor(retryDelay, TimeUnit.MILLISECONDS)
                            .execute(() -> sendMessageAsync(chatMessage, attempt + 1, maxRetryAttempts, retryDelay)
                                    .whenComplete((res, ex) -> {
                                        if (ex != null) {
                                            retryFuture.completeExceptionally(ex);
                                        } else {
                                            retryFuture.complete(null);  // 정상 처리
                                        }
                    }));
                    return retryFuture;
                } else {
                    // 재시도 후 실패시 예외 처리
                    throw new RuntimeException("Failed to send message after multiple attempts", e);
                }
            } else {
                log.info("Produced message: {}", chatMessage);
                return null;  // 전송 성공
            }
        }).thenCompose(result -> CompletableFuture.completedFuture(null));
    }

    private CompletableFuture<Void> sendMessageAsync(NotificationDto notificationDto, int attempt, int maxRetryAttempts, long retryDelay) {
        // Kafka로 메시지 전송
        CompletableFuture<SendResult<String, NotificationDto>> sendResultFuture = notificationKafkaTemplate.send(notificationTopic, notificationDto);

        // 전송 결과 처리
        return sendResultFuture.handleAsync((result, e) -> {
            if (e != null) {
                log.error("Attempt {} failed to send message: {}", attempt + 1, notificationDto, e);

                if (attempt + 1 < maxRetryAttempts) {
                    log.warn("Retrying in {} ms...", retryDelay);
                    // 재시도
                    CompletableFuture<Void> retryFuture = new CompletableFuture<>();
                    CompletableFuture.delayedExecutor(retryDelay, TimeUnit.MILLISECONDS)
                            .execute(() -> sendMessageAsync(notificationDto, attempt + 1, maxRetryAttempts, retryDelay)
                                    .whenComplete((res, ex) -> {
                                        if (ex != null) {
                                            retryFuture.completeExceptionally(ex);
                                        } else {
                                            retryFuture.complete(null);  // 정상 처리
                                        }
                                    }));
                    return retryFuture;
                } else {
                    // 재시도 후 실패시 예외 처리
                    throw new RuntimeException("Failed to send message after multiple attempts", e);
                }
            } else {
                log.info("Produced message: {}", notificationDto);
                return null;  // 전송 성공
            }
        }).thenCompose(result -> CompletableFuture.completedFuture(null));
    }
}
