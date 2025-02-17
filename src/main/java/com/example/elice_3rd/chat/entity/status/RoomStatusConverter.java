package com.example.elice_3rd.chat.entity.status;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoomStatusConverter extends AbstractLegacyEnumAttributeConverter<RoomStatus> {
    public RoomStatusConverter() {
        super(RoomStatus.class);
    }
}
