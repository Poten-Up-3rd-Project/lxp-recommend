package com.lxp.recommend.infrastructure.external.common;

import com.lxp.common.infrastructure.exception.ErrorResponse;

/**
 * Internal API 응답 공통 포맷
 * Member, Course, Enrollment BC와의 통신에 사용
 */
public record InternalApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse error  // ✅ common-lib ErrorResponse 사용
) {
    /**
     * 성공 응답 생성
     */
    public static <T> InternalApiResponse<T> success(T data) {
        return new InternalApiResponse<>(true, data, null);
    }

    /**
     * 실패 응답 생성
     */
    public static <T> InternalApiResponse<T> error(ErrorResponse error) {
        return new InternalApiResponse<>(false, null, error);
    }

    /**
     * 실패 응답 생성 (간편 버전)
     */
    public static <T> InternalApiResponse<T> error(String code, String message, String group) {
        ErrorResponse error = new ErrorResponse(code, message, group);
        return new InternalApiResponse<>(false, null, error);
    }
}
