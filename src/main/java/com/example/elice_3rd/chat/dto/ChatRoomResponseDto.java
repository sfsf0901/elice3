package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.chat.entity.mysql.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import lombok.*;

@Getter
@Setter
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;

    private Long memberId;

    private RoomStatus roomStatus;

    public static ChatRoomResponseDto toDto(ChatRoom chatRoom, Long memberId) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .memberId(memberId)
                .roomStatus(chatRoom.getRoomStatus())
                .build();
    }
}
