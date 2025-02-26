package com.example.elice_3rd.common.handler;

import com.example.elice_3rd.common.ErrorCode;
import com.example.elice_3rd.common.ErrorResponse;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.common.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
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
        log.error("UnauthorizedException : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchDataException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchData(NoSuchDataException e) {
        log.error("NoSuchDataException : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.error("IllegalArgumentException : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
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
