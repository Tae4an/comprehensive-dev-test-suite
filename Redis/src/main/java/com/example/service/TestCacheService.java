package com.example.service;

import com.example.dto.TestCacheResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public TestCacheResponse getExpensiveOperation(String key) {
        // 1. 캐시 확인
        TestCacheResponse cachedResponse = (TestCacheResponse) redisTemplate.opsForValue().get(key);
        if (cachedResponse != null) {
            log.info("Cache hit for key: {}", key);
            return cachedResponse;
        }

        log.info("Cache miss for key: {}", key);
        // 2. 무거운 작업 시뮬레이션
        TestCacheResponse response = performExpensiveOperation();
        
        // 3. 결과 캐싱 (1시간)
        redisTemplate.opsForValue().set(key, response, 1, TimeUnit.HOURS);
        
        return response;
    }

    private TestCacheResponse performExpensiveOperation() {
        // 무거운 작업 시뮬레이션 (2초 대기)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
        return new TestCacheResponse("expensive operation result", System.currentTimeMillis());
    }
}