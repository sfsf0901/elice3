package com.example.elice_3rd.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {
    private int status;
    private String resultMessage;
    private List<FieldError> errors;
    private String reason;

    @Builder
    protected ErrorResponse(final ErrorCode errorCode) {
        resultMessage = errorCode.getMessage();
        status = errorCode.getStatus();
        errors = new ArrayList<>();
    }

    @Builder
    protected ErrorResponse(final ErrorCode errorCode, final String reason) {
        resultMessage = errorCode.getMessage();
        status = errorCode.getStatus();
        this.reason = reason;
    }

    @Builder
    protected ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
        resultMessage = errorCode.getMessage();
        status = errorCode.getStatus();
        this.errors = errors;
    }

    public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bindingResult) {
        return new ErrorResponse(errorCode, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(final ErrorCode errorCode, final String reason) {
        return new ErrorResponse(errorCode, reason);
    }

    @Getter
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }

        @Builder
        FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
    }
}
