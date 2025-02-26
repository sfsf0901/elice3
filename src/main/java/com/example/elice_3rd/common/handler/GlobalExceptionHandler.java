package com.example.elice_3rd.common.handler;

import com.example.elice_3rd.common.ErrorCode;
import com.example.elice_3rd.common.ErrorResponse;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e){
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchDataException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchData(NoSuchDataException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, toBodyResponse(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String toBodyResponse(BindException e) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            stringBuilder.append(error.getField()).append(":");
            stringBuilder.append(error.getDefaultMessage());
            stringBuilder.append("\n");
        });

        String response = String.valueOf(stringBuilder);
        log.error(response);
        return response;
    }
}
