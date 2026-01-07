package com.lxp.recommend.domain.model.ids;

import java.util.Objects;

/**
 * 강좌 식별자 값 객체 (Value Object)
 * JPA 어노테이션 없음 - 순수 도메인 개념
 */
public class CourseId {

    private final String value;

    protected CourseId() {
        this.value = null; // JPA 재구성용
    }

    public CourseId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseId cannot be null or blank");
        }
        this.value = value;
    }

    public static CourseId of(String value) {
        return new CourseId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId = (CourseId) o;
        return Objects.equals(value, courseId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
