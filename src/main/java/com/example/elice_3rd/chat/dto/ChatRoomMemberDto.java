package com.example.elice_3rd.chat.dto;

import com.example.elice_3rd.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomMemberDto {

    private String name;

    public static ChatRoomMemberDto toDto(Member member) {
        return ChatRoomMemberDto.builder()
                .name(member.getName())
                .build();
    }
}
