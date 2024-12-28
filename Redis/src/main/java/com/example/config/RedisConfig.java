package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 연결 및 설정을 위한 Configuration 클래스
 * Redis 연결 설정, 직렬화 방식, 템플릿 설정 등을 담당
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;  // Redis 서버 호스트

    @Value("${spring.data.redis.port}")
    private int port;     // Redis 서버 포트

    @Value("${spring.data.redis.password}")
    private String password;  // Redis 서버 비밀번호

    /**
     * Redis 연결을 위한 ConnectionFactory 빈 설정
     * Lettuce 라이브러리를 사용하여 Redis 연결 구성
     * @return LettuceConnectionFactory Redis 연결을 위한 Factory 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis Standalone 모드 설정 (단일 서버)
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);

        // Lettuce 커넥션 팩토리 설정
        // setShareNativeConnection(true): 네이티브 커넥션을 공유하여 리소스 효율성 증가
        // 여러 RedisConnection이 단일 네이티브 connection을 공유할 수 있게 함
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.setShareNativeConnection(true);
        return connectionFactory;
    }

    /**
     * Redis 작업을 위한 RedisTemplate 빈 설정
     * 다양한 Redis 데이터 타입 조작을 위한 템플릿 제공
     * @return RedisTemplate String 키와 Object 값을 다루는 템플릿
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Key Serializer 설정: 모든 키는 String으로 처리
        template.setKeySerializer(new StringRedisSerializer());
        // Value Serializer 설정: 값은 JSON 형식으로 직렬화
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        // Hash 작업을 위한 Serializer 설정
        // Hash의 키는 String으로, 값은 JSON으로 직렬화
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return template;
    }

    /**
     * 이미지 캐싱을 위한 RedisTemplate 빈 설정
     * 바이트 배열을 직렬화/역직렬화하기 위한 템플릿
     * @return RedisTemplate String 키와 byte[] 값을 다루는 템플릿
     */
    @Bean
    public RedisTemplate<String, byte[]> redisImageTemplate() {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Key는 문자열로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        // Value는 바이트 배열 그대로 저장
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        // Hash 작업을 위한 Serializer 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }
}