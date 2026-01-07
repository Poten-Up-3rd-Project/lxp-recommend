package com.lxp.recommend.domain.exception;

/**
 * 추천 아이템에 중복 강좌가 포함될 때 발생
 */
public class DuplicateCourseException extends RecommendException {

    public DuplicateCourseException(String message) {
        super(message);
    }
}
