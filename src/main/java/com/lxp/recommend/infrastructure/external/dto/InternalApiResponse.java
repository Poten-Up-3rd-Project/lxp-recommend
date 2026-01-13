package com.lxp.recommend.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Internal API 공통 응답 래퍼
 *
 * @param <T> 실제 데이터 타입
 */
@Getter
@NoArgsConstructor
public class InternalApiResponse<T> {

    private boolean success;
    private T data;
    private Object error;
    private LocalDateTime timestamp;

    public InternalApiResponse(boolean success, T data, Object error, LocalDateTime timestamp) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = timestamp;
    }
}
