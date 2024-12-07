package com.example.exception;

import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RedisExceptionHandler {

    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<String> handleRedisConnectionException(RedisConnectionException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Redis connection failed: " + e.getMessage());
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<String> handleRedisException(RedisException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Redis operation failed: " + e.getMessage());
    }
}
