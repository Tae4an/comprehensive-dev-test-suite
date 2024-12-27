package com.example.controller;

import com.example.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
@Tag(name = "Redis Controller", description = "Redis 연동 API")
public class RedisController {

    private final RedisService redisService;

    @Operation(summary = "문자열 저장", description = "Redis에 문자열 값을 저장합니다.")
    @PostMapping("/string")
    public ResponseEntity<String> setString(
            @RequestParam String key,
            @RequestParam String value) {
        redisService.setString(key, value);
        return ResponseEntity.ok("Success");
    }

    @Operation(summary = "문자열 조회", description = "Redis에서 문자열 값을 조회합니다.")
    @GetMapping("/string")
    public ResponseEntity<Object> getString(@RequestParam String key) {
        return ResponseEntity.ok(redisService.getString(key));
    }

    @Operation(summary = "리스트에 추가", description = "Redis 리스트에 값을 추가합니다.")
    @PostMapping("/list")
    public ResponseEntity<String> addToList(
            @RequestParam String key,
            @RequestParam String value) {
        redisService.addToList(key, value);
        return ResponseEntity.ok("Success");
    }

    @Operation(summary = "리스트 조회", description = "Redis 리스트의 모든 값을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<Object>> getList(@RequestParam String key) {
        return ResponseEntity.ok(redisService.getList(key));
    }

    @Operation(summary = "만료시간 설정", description = "Redis에 만료시간이 있는 값을 저장합니다.")
    @PostMapping("/expire")
    public ResponseEntity<String> setWithExpiration(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam long timeout) {
        redisService.setWithExpiration(key, value, timeout, TimeUnit.SECONDS);
        return ResponseEntity.ok("Success");
    }

    /**
     * 이미지 캐싱 API
     * MultipartFile로 전달된 이미지를 Redis에 캐시
     */
    @Operation(summary = "이미지 캐싱", description = "이미지 파일을 Redis에 캐시합니다.")
    @PostMapping("/image")
    public ResponseEntity<String> cacheImage(
            @RequestParam("key") String key,
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] imageData = file.getBytes();
            redisService.cacheImage(key, imageData);
            return ResponseEntity.ok("Image cached successfully");
        } catch (IOException e) {
            log.error("Failed to process image file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process image file");
        }
    }

    /**
     * 캐시된 이미지 조회 API
     * 저장된 이미지를 바이트 배열로 반환
     */
    @Operation(summary = "이미지 조회", description = "캐시된 이미지를 조회합니다.")
    @GetMapping("/image")
    public ResponseEntity<byte[]> getCachedImage(@RequestParam String key) {
        byte[] imageData = redisService.getCachedImage(key);
        if (imageData != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 이미지 캐시 삭제 API
     */
    @Operation(summary = "이미지 캐시 삭제", description = "캐시된 이미지를 삭제합니다.")
    @DeleteMapping("/image")
    public ResponseEntity<String> deleteImageCache(@RequestParam String key) {
        redisService.deleteImageCache(key);
        return ResponseEntity.ok("Image cache deleted successfully");
    }

    /**
     * 만료시간이 있는 이미지 캐싱 API
     */
    @Operation(summary = "이미지 캐싱 (만료시간 설정)",
            description = "이미지를 지정된 만료시간과 함께 캐시합니다.")
    @PostMapping("/image/expire")
    public ResponseEntity<String> cacheImageWithExpiration(
            @RequestParam("key") String key,
            @RequestParam("file") MultipartFile file,
            @RequestParam("timeout") long timeout) {
        try {
            byte[] imageData = file.getBytes();
            redisService.cacheImageWithExpiration(key, imageData, timeout, TimeUnit.SECONDS);
            return ResponseEntity.ok("Image cached successfully with expiration");
        } catch (IOException e) {
            log.error("Failed to process image file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process image file");
        }
    }
}