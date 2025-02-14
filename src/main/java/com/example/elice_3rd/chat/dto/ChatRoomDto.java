package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {

    private Long chatRoomId;

    private RoomStatus roomStatus;

    private String opponentName;

    public static ChatRoomDto toDto(ChatRoom chatRoom, Long memberId) {
        Member opponent = chatRoom.getMembers().stream()
                .filter(member -> !member.getMemberId().equals(memberId)) // 해당 유저와 다른 멤버 찾기
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No opponent found in the chat room"));

        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .roomStatus(chatRoom.getRoomStatus())
                .opponentName(opponent.getName())
                .build();
    }
}
