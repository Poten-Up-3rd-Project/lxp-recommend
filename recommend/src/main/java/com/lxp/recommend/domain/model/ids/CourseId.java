package com.lxp.recommend.domain.model.ids;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseId implements Serializable {
    private Long id;

    protected CourseId() {}

    public CourseId(Long id) {
        if (id == null) throw new IllegalArgumentException("CourseId cannot be null");
        this.id = id;
    }

    public static CourseId of(Long id) {
        return new CourseId(id);
    }

    public Long getValue() {
        return id;
    }

    // equals, hashCode, toString 생략 (MemberId와 동일 패턴)
}
