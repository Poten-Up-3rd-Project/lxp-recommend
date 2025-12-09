package com.lxp.recommend.domain.model.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MemberId implements Serializable {

    @Column(name = "member_id", nullable = false)
    private String id; // Long -> String 변경 (UUID 수용)

    protected MemberId() {}

    public MemberId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("MemberId cannot be null or blank");
        }
        this.id = id;
    }

    public static MemberId of(String id) {
        return new MemberId(id);
    }

    public String getValue() {
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
        return id;
    }
}
