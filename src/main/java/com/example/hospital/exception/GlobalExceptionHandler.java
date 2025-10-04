package com.example.hospital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static class ApiError {
        public final String message;
        public final int status;
        public final String timestamp;
        public final Map<String, String> details;

        public ApiError(String message, int status, Map<String, String> details) {
            this.message = message;
            this.status = status;
            this.timestamp = OffsetDateTime.now().toString();
            this.details = details;
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        ex.printStackTrace();

        ApiError err = new ApiError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        ApiError err = new ApiError("Validation failed", HttpStatus.BAD_REQUEST.value(), errors);
        return ResponseEntity.badRequest().body(err);
    }
}
