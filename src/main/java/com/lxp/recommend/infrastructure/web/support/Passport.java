package com.lxp.recommend.infrastructure.web.support;

/**
 * Gateway에서 전달한 사용자 인증 정보
 * X-Passport 헤더의 JWT를 파싱한 결과
 */
public record Passport(
        String userId,
        String role,
        String traceId
) {
    public Passport {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or blank");
        }
    }
}
