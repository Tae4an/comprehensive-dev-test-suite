package com.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 서버의 상태를 모니터링하기 위한 Health Indicator 클래스
 * Spring Boot Actuator의 /actuator/health 엔드포인트를 통해 Redis 상태 확인 가능
 */
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator extends AbstractHealthIndicator {  // Spring Boot의 Health Check 추상 클래스 상속

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 서버의 상태를 체크하는 메소드
     * @param builder Health 상태를 구성하기 위한 빌더
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            // "health" 키로 Redis 서버 연결 상태 확인
            // 실제 값을 가져오는 것이 아닌 연결 테스트 목적
            redisTemplate.opsForValue().get("health");
            builder.up();  // Redis 서버가 정상이면 UP 상태로 설정
        } catch (Exception e) {
            builder.down(e);  // 연결 실패 시 DOWN 상태로 설정하고 예외 정보 포함
        }
    }
}