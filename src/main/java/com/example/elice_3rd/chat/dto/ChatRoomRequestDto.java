package com.example.elice_3rd.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ChatRoomRequestDto {

    private Set<Long> memberIds;
}
