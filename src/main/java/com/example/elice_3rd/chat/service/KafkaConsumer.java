package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.entity.Notification;
import com.example.elice_3rd.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private  final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.chatTopic}", containerFactory = "chatMessageListenerFactory")
    public void listen(@Payload ChatMessage chatMessage) {
        try {
            log.info("Received message : {}", chatMessage);
            notificationService.sendNotificationToKafka(chatMessage);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", chatMessage, e);
        }
    }

    @KafkaListener(topics = "${spring.kafka.notificationTopic}", containerFactory = "notificationListenerFactory")
    public void listen(@Payload NotificationDto notificationDto) {
        try {
            log.info("Received notification message : {}", notificationDto);
            // 알림을 SSE로 전송
            notificationService.sendNotification(notificationDto);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", notificationDto, e);
        }
    }
}
