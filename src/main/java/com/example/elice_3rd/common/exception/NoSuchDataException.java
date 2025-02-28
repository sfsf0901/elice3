package com.example.elice_3rd.common.exception;

import lombok.Getter;

@Getter
public class NoSuchDataException extends RuntimeException {
    public NoSuchDataException(String message) {
        super(message);
    }
}
