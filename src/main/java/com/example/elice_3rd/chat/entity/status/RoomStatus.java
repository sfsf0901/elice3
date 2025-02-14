package com.example.elice_3rd.chat.entity.status;

import lombok.Getter;

@Getter
public enum RoomStatus implements LegacyCommonType {
    ACTIVE(0, "활성화"),
    INACTIVE(1, "비활성화");

    private final int code;
    private final String description;

    RoomStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RoomStatus of(int code) {
        for (RoomStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid RoomStatus code: " + code);
    }
}
