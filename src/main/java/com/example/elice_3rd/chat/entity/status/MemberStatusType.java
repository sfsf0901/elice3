package com.example.elice_3rd.chat.entity.status;

import lombok.Getter;

@Getter
public enum MemberStatusType implements LegacyCommonType {
    ONLINE(0, "온라인"),
    OFFLINE(1,"오프라인"),
    LEFT(2, "채팅방을 나감");

    private final int code;
    private final String description;

    MemberStatusType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MemberStatusType of(int code) {
        for (MemberStatusType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MemberStatusType code: " + code);
    }
}
