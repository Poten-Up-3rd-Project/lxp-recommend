package com.lxp.recommend.domain.exception;

/**
 * 추천 컨텍스트가 유효하지 않을 때 발생
 */
public class InvalidRecommendContextException extends RecommendException {

    public InvalidRecommendContextException(String message) {
        super(message);
    }
}