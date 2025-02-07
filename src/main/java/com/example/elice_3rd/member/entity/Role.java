package com.example.elice_3rd.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반 사용자"), DOCTOR("ROLE_DOCTOR", "의사");

    private final String key;
    private final String title;
}
