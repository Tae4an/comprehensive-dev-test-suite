package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Comprehensive suite of development tests API Documentation")
                        .description("""
                         종합 개발 테스트 API 문서
                        
                        1. WebRTC 화상회의
                           - 화상회의방 생성 및 관리
                           - WebSocket 시그널링
                        """)
                        .version("1.0.0"))
                .addServersItem(new Server().url("/").description("Default Server URL"));
    }
}