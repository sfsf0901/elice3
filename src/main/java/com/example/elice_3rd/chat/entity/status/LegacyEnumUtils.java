package com.example.elice_3rd.chat.entity.status;

public class LegacyEnumUtils {
    public static <E extends Enum<E> & LegacyCommonType> E of(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
