package com.lxp.recommend.infrastructure.external.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Internal API 공통 응답 래퍼
 *
 * 모든 내부 BC(Member, Course, Enrollment)가 동일한 응답 구조 사용
 *
 * @param <T> 실제 데이터 타입
 */
public record InternalApiResponse<T>(
        boolean success,
        T data,
        Object error,
        LocalDateTime timestamp
) {
}
