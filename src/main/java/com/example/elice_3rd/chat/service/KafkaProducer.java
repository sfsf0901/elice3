package com.example.elice_3rd.chat.service;

import com.example.elice_3rd.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    public void sendMessage(ChatMessage chatMessage) {
        kafkaTemplate.send(topic, chatMessage);
        log.info("Produced message : " + chatMessage);
    }
}
