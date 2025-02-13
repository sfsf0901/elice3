package com.example.elice_3rd.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRoomRequest {

    private Long chatRoomId;

    private Long memberId;
}
