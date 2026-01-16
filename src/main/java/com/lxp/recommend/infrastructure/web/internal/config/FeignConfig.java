package com.lxp.recommend.infrastructure.web.internal.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.Logger;
import feign.Request;
import feign.Retryer;

/**
 * Feign Client 전역 설정
 */
@Configuration
@EnableFeignClients(basePackages = "com.lxp.recommend.infrastructure.web.internal.client")
public class FeignConfig {

    /**
     * Feign 로그 레벨 설정
     * - NONE: 로그 없음 (운영 환경)
     * - BASIC: 요청 메서드, URL, 응답 상태, 실행 시간만 로깅
     * - HEADERS: BASIC + 요청/응답 헤더
     * - FULL: HEADERS + 요청/응답 본문 (개발 환경)
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // 개발 환경에서는 FULL, 운영에서는 BASIC 권장
    }

    /**
     * Feign Timeout 설정
     * - connectTimeoutMillis: 연결 타임아웃 (ms)
     * - readTimeoutMillis: 읽기 타임아웃 (ms)
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                3000,  // connectTimeoutMillis: 3초
                5000   // readTimeoutMillis: 5초
        );
    }

    /**
     * Feign Retry 설정
     * - period: 재시도 간격 (ms)
     * - maxPeriod: 최대 재시도 간격 (ms)
     * - maxAttempts: 최대 재시도 횟수
     *
     * 현재 설정: 재시도 비활성화 (NEVER)
     * 필요 시 Retryer.Default(100, 1000, 3)으로 변경
     */
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;  // 재시도 없음 (빠른 실패)
        // return new Retryer.Default(100, 1000, 3);  // 재시도 활성화 시
    }
}
