package com.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RedisOperationAspect {

    // RedisTemplate의 모든 메소드 호출을 가로채서 실행 시간 측정
    @Around("execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
    public Object logRedisOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();  // 실제 Redis 작업 실행
        long timeTaken = System.currentTimeMillis() - startTime;

        // 작업 이름과 소요 시간 로깅
        log.info("Redis operation: {}, Time taken: {}ms",
            joinPoint.getSignature().getName(), timeTaken);
            
        return result;
    }
}