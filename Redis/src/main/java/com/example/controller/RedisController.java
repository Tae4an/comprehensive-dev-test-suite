package com.example.controller;

import com.example.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
}