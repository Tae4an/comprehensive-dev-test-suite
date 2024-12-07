package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    // RedisTemplate 주입 - Redis 작업을 위한 핵심 클래스
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis에 문자열 값을 저장하는 메소드
     * @param key Redis에 저장될 키
     * @param value 저장할 값
     */
    public void setString(String key, String value) {
        try {
            // opsForValue(): Redis의 String 타입 작업을 위한 operations
            // set(): 키-값 쌍을 Redis에 저장
            redisTemplate.opsForValue().set(key, value);
            log.info("Successfully set key: {}", key);
        } catch (Exception e) {
            log.error("Error setting value in Redis", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Redis에서 문자열 값을 조회하는 메소드
     * @param key 조회할 키
     * @return 저장된 값 (없으면 null)
     */
    public Object getString(String key) {
        try {
            // get(): 주어진 키에 해당하는 값을 조회
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting value from Redis", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Redis List에 값을 추가하는 메소드
     * @param key List의 키
     * @param value 추가할 값
     */
    public void addToList(String key, Object value) {
        try {
            // opsForList(): Redis의 List 타입 작업을 위한 operations
            // rightPush(): List의 오른쪽 끝에 값을 추가 (RPUSH)
            redisTemplate.opsForList().rightPush(key, value);
            log.info("Successfully added to list: {}", key);
        } catch (Exception e) {
            log.error("Error adding to list in Redis", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Redis List의 모든 값을 조회하는 메소드
     * @param key List의 키
     * @return List에 저장된 모든 값
     */
    public List<Object> getList(String key) {
        try {
            // range(): List의 범위를 조회 (0부터 -1은 전체 범위)
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            log.error("Error getting list from Redis", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    /**
     * Redis에 만료시간이 있는 값을 저장하는 메소드
     * @param key 저장할 키
     * @param value 저장할 값
     * @param timeout 만료 시간
     * @param unit 시간 단위 (예: SECONDS, MINUTES)
     */
    public void setWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        try {
            // set with timeout: 키-값 쌍을 저장하고 만료시간 설정
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.info("Successfully set key with expiration: {}", key);
        } catch (Exception e) {
            log.error("Error setting value with expiration in Redis", e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
}