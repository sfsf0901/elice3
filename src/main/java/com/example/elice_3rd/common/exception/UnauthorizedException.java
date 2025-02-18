package com.example.elice_3rd.common.exception;

import lombok.RequiredArgsConstructor;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }
}
