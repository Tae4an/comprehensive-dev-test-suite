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

    // 일반 데이터 처리를 위한 RedisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    // 이미지 데이터 처리를 위한 RedisTemplate (바이트 배열 전용)
    private final RedisTemplate<String, byte[]> redisImageTemplate;

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

    /**
     * 이미지를 Redis에 캐싱하는 메소드
     * 바이트 배열로 전달된 이미지 데이터를 Redis에 저장
     *
     * @param key 이미지를 식별하기 위한 고유 키 값
     * @param imageData 캐싱할 이미지의 바이트 배열 데이터
     * @throws RuntimeException Redis 작업 실패시 발생
     */
    public void cacheImage(String key, byte[] imageData) {
        try {
            // 이미지 전용 RedisTemplate을 사용하여 바이트 배열 저장
            redisImageTemplate.opsForValue().set(key, imageData);
            log.info("Successfully cached image with key: {}", key);
        } catch (Exception e) {
            log.error("Error caching image with key: {}", key, e);
            throw new RuntimeException("Redis image caching failed", e);
        }
    }

    /**
     * Redis에서 캐시된 이미지를 조회하는 메소드
     * 저장된 이미지를 바이트 배열 형태로 반환
     *
     * @param key 조회할 이미지의 식별 키
     * @return 이미지 바이트 배열, 캐시 미스의 경우 null 반환
     * @throws RuntimeException Redis 작업 실패시 발생
     */
    public byte[] getCachedImage(String key) {
        try {
            // 이미지 전용 RedisTemplate을 사용하여 바이트 배열 조회
            byte[] cachedData = redisImageTemplate.opsForValue().get(key);
            if (cachedData != null) {
                log.info("Cache hit for image key: {}", key);
                return cachedData;
            }
            // 캐시 미스 로깅
            log.info("Cache miss for image key: {}", key);
            return null;
        } catch (Exception e) {
            log.error("Error retrieving cached image for key: {}", key, e);
            throw new RuntimeException("Redis image retrieval failed", e);
        }
    }

    /**
     * 이미지를 만료시간과 함께 캐싱하는 메소드
     * 지정된 시간이 지나면 자동으로 삭제되는 이미지 캐시 생성
     *
     * @param key 이미지 식별 키
     * @param imageData 캐싱할 이미지 바이트 배열
     * @param timeout 캐시 만료 시간
     * @param unit 시간 단위 (예: SECONDS, MINUTES, HOURS)
     * @throws RuntimeException Redis 작업 실패시 발생
     */
    public void cacheImageWithExpiration(String key, byte[] imageData, long timeout, TimeUnit unit) {
        try {
            // 이미지와 만료시간을 함께 설정
            redisImageTemplate.opsForValue().set(key, imageData, timeout, unit);
            log.info("Successfully cached image with expiration. Key: {}, Timeout: {} {}",
                    key, timeout, unit);
        } catch (Exception e) {
            log.error("Error caching image with expiration. Key: {}", key, e);
            throw new RuntimeException("Redis image caching with expiration failed", e);
        }
    }

    /**
     * 캐시된 이미지를 삭제하는 메소드
     * 더 이상 필요하지 않은 이미지 캐시를 명시적으로 제거
     *
     * @param key 삭제할 이미지의 식별 키
     * @throws RuntimeException Redis 작업 실패시 발생
     */
    public void deleteImageCache(String key) {
        try {
            Boolean deleted = redisImageTemplate.delete(key);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("Successfully deleted cached image with key: {}", key);
            } else {
                // 삭제할 캐시가 존재하지 않는 경우
                log.warn("No image found to delete for key: {}", key);
            }
        } catch (Exception e) {
            log.error("Error deleting cached image for key: {}", key, e);
            throw new RuntimeException("Redis image deletion failed", e);
        }
    }
}