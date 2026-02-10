package com.lxp.recommend.domain;

import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RecommendCourseTest {

    @Test
    @DisplayName("강의를 생성할 수 있다")
    void createCourse() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .tags(List.of(1L, 2L, 3L))
                .level(Level.MIDDLE)
                .instructorId("instructor-1")
                .build();

        assertThat(course.getId()).isEqualTo("course-1");
        assertThat(course.getTags()).containsExactly(1L, 2L, 3L);
        assertThat(course.getLevel()).isEqualTo(Level.MIDDLE);
        assertThat(course.getInstructorId()).isEqualTo("instructor-1");
        assertThat(course.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("강의를 비활성화할 수 있다")
    void deactivate() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .level(Level.JUNIOR)
                .instructorId("instructor-1")
                .build();

        course.deactivate();

        assertThat(course.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("강의를 활성화할 수 있다")
    void activate() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .level(Level.JUNIOR)
                .instructorId("instructor-1")
                .status(Status.INACTIVE)
                .build();

        course.activate();

        assertThat(course.getStatus()).isEqualTo(Status.ACTIVE);
    }
}
