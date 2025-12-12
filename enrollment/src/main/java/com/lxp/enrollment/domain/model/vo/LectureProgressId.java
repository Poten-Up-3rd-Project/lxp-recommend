package com.lxp.enrollment.domain.model.vo;

import java.util.Objects;

public record LectureProgressId(Long value) {
    public LectureProgressId {
        if(value == null) {
            throw new IllegalArgumentException("LectureStudyId cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureProgressId that = (LectureProgressId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
