package com.lxp.recommend.domain.model.ids;

import java.util.Objects;

/**
 * 회원 식별자 값 객체 (Value Object)
 * JPA 어노테이션 없음 - 순수 도메인 개념
 */
public class MemberId {

    private final String value;

    protected MemberId() {
        this.value = null; // JPA 재구성용, 실제로는 사용 안 됨
    }

    public MemberId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MemberId cannot be null or blank");
        }
        this.value = value;
    }

    public static MemberId of(String value) {
        return new MemberId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberId memberId = (MemberId) o;
        return Objects.equals(value, memberId.value);
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
