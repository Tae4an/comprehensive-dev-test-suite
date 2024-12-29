package com.example;

import com.example.dto.TestCacheResponse;
import com.example.service.TestCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestCacheServiceTest {

    @Autowired
    private TestCacheService testCacheService;

    @Test
    void cacheTest() {
        String testKey = "test:operation";

        // 첫 번째 호출 - 캐시 미스
        long startTime1 = System.currentTimeMillis();
        TestCacheResponse response1 = testCacheService.getExpensiveOperation(testKey);
        long duration1 = System.currentTimeMillis() - startTime1;

        // 두 번째 호출 - 캐시 히트
        long startTime2 = System.currentTimeMillis();
        TestCacheResponse response2 = testCacheService.getExpensiveOperation(testKey);
        long duration2 = System.currentTimeMillis() - startTime2;

        // 검증
        assertThat(duration1).isGreaterThan(2000); // 첫 호출은 2초 이상
        assertThat(duration2).isLessThan(100);     // 두 번째 호출은 100ms 미만
        assertThat(response1.getData()).isEqualTo(response2.getData()); // 데이터 일치
        assertThat(response1.getTimestamp()).isEqualTo(response2.getTimestamp()); // 타임스탬프 일치
    }
}