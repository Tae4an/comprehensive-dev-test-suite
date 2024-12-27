package com.example.exception;

import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
@ControllerAdvice
@Slf4j
public class RedisExceptionHandler {

    // 기존 Redis 연결 예외 처리
    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<String> handleRedisConnectionException(RedisConnectionException e) {
        log.error("Redis connection failed", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Redis connection failed: " + e.getMessage());
    }

    // 기존 Redis 작업 예외 처리
    @ExceptionHandler(RedisException.class)
    public ResponseEntity<String> handleRedisException(RedisException e) {
        log.error("Redis operation failed", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Redis operation failed: " + e.getMessage());
    }

    // 이미지 처리 관련 예외 처리
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        log.error("Failed to process image file", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to process image file: " + e.getMessage());
    }

    // 이미지 크기 초과 예외 처리
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("Image file size exceeds the maximum limit", e);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("Image file size exceeds the maximum limit");
    }

    // 이미지 형식 관련 예외 처리
    @ExceptionHandler(InvalidImageFormatException.class)
    public ResponseEntity<String> handleInvalidImageFormatException(InvalidImageFormatException e) {
        log.error("Invalid image format", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid image format: " + e.getMessage());
    }

    // 일반적인 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }
}