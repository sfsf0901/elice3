package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private String chatMessageId;

    private Long chatRoomId;

    private Long senderId;

    private String message;

    private LocalDateTime createdDate;

    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .chatMessageId(chatMessage.getChatMessageId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSenderId())
                .message(chatMessage.getMessage())
                .createdDate(chatMessage.getCreatedDate())
                .build();
    }

    public ChatMessage toEntity () {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .message(message)
                .build();
    }
}
