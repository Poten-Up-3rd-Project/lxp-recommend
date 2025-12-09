package com.lxp.recommend.domain.model.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseId implements Serializable {

    @Column(name = "course_id", nullable = false)
    private String id; // Long -> String 변경 (UUID 수용)

    protected CourseId() {}

    public CourseId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("CourseId cannot be null or blank");
        }
        this.id = id;
    }

    public static CourseId of(String id) {
        return new CourseId(id);
    }

    public String getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId = (CourseId) o;
        return Objects.equals(id, courseId.id);
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
