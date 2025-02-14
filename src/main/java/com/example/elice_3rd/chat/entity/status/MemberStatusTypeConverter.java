package com.example.elice_3rd.chat.entity.status;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MemberStatusTypeConverter extends AbstractLegacyEnumAttributeConverter<MemberStatusType> {
    public MemberStatusTypeConverter() {
        super(MemberStatusType.class);
    }
}
