package com.example.elice_3rd.chat.config;

import com.example.elice_3rd.chat.entity.mongodb.ChatMessage;
import com.example.elice_3rd.notification.dto.NotificationDto;
import com.example.elice_3rd.notification.entity.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackages;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessage> chatMessageListenerFactory(
            ConsumerFactory<String, ChatMessage> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDto> notificationListenerFactory(
            ConsumerFactory<String, NotificationDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, NotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChatMessage> chatMessageConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ChatMessage.class.getName());  // default type 설정

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    public ConsumerFactory<String, NotificationDto> notificationConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationDto.class.getName());  // default type 설정

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    public ProducerFactory<String, ChatMessage> chatMessageProducerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public ProducerFactory<String, NotificationDto> notificationProducerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public KafkaTemplate<String, ChatMessage> chatMessageKafkaTemplate() {
        return new KafkaTemplate<>(chatMessageProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, NotificationDto> notificationKafkaTemplate() {
        return new KafkaTemplate<>(notificationProducerFactory());
    }
}
