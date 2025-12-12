package com.lxp.enrollment.domain.model.vo;

import java.util.Objects;

public record CourseProgressId(Long value) {
    public CourseProgressId {
        if(value == null) {
            throw new IllegalArgumentException("CourseStudyId cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseProgressId that = (CourseProgressId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
