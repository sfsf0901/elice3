package com.example.elice_3rd.common;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
public class ErrorResponse {
    private int status;
    private String resultMessage;
    private List<FieldError> errors;
    private String reason;
}
