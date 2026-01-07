package com.lxp.recommend.domain.exception;

/**
 * 추천 아이템이 최대 개수(10개)를 초과할 때 발생
 */
public class RecommendLimitExceededException extends RecommendException {

    public RecommendLimitExceededException(String message) {
        super(message);
    }
}
