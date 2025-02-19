package com.example.elice_3rd.chat.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatRoomDeletedEvent extends ApplicationEvent {

    private final Long chatRoomId;

    public ChatRoomDeletedEvent(Object source, Long chatRoomId) {
        super(source);
        this.chatRoomId = chatRoomId;
    }
}
