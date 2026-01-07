package com.lxp.recommend.domain.exception;

/**
 * 추천 도메인에서 발생하는 비즈니스 예외
 */
public class RecommendException extends RuntimeException {

    public RecommendException(String message) {
        super(message);
    }

    public RecommendException(String message, Throwable cause) {
        super(message, cause);
    }
}
