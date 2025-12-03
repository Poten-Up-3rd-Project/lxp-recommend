package com.lxp.recommend.domain.model.ids;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // JPA에서 임베디드 타입으로 사용 가능하게 설정
public class MemberId implements Serializable {

    private Long id;

    protected MemberId() {} // JPA용 기본 생성자

    public MemberId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("MemberId cannot be null");
        }
        this.id = id;
    }

    public static MemberId of(Long id) {
        return new MemberId(id);
    }

    public Long getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberId memberId = (MemberId) o;
        return Objects.equals(id, memberId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
