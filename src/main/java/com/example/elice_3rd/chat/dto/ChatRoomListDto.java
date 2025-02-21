package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.chat.entity.ChatRoom;
import com.example.elice_3rd.chat.entity.status.RoomStatus;
import com.example.elice_3rd.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListDto {

    private Long chatRoomId;

    private RoomStatus roomStatus;

    private String opponentName;

    private String message;

    private LocalDateTime lastModifiedDate;

    private Long unreadMessagesCount;

    public static ChatRoomListDto toDto(ChatRoom chatRoom, Long memberId, String message, LocalDateTime lastModifiedDate, Long unreadMessagesCount) {
        Member opponent = chatRoom.getMembers().stream()
                .filter(member -> !member.getMemberId().equals(memberId)) // 해당 유저와 다른 멤버 찾기
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No opponent found in the chat room"));

        return ChatRoomListDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .roomStatus(chatRoom.getRoomStatus())
                .opponentName(opponent.getName())
                .message(message)
                .lastModifiedDate(lastModifiedDate)
                .unreadMessagesCount(unreadMessagesCount)
                .build();
    }
}
