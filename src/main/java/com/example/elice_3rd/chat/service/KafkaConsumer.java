package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.entity.ChatMessage;
import com.example.elice_3rd.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ChatMessage chatMessage) {
        try {
            log.info("Received message : {}", chatMessage);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", chatMessage, e);
        }
    }
}
