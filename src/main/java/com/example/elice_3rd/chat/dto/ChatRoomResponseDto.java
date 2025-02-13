package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import lombok.*;

@Getter
@Setter
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;

    private RoomStatus roomStatus;

    public static ChatRoomResponseDto toDto(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .roomStatus(chatRoom.getRoomStatus())
                .build();
    }
}
