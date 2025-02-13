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

    private Long chatRoom;

    private Long sender;

    private String message;

    private LocalDateTime createdDate;

    public static ChatMessageDto toDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .chatRoom(chatMessage.getChatRoom())
                .sender(chatMessage.getSender())
                .message(chatMessage.getMessage())
                .createdDate(chatMessage.getCreatedDate())
                .build();
    }

    public ChatMessage toEntity () {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(message)
                .build();
    }
}
