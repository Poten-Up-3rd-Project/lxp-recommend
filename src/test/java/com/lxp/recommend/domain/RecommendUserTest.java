package com.lxp.recommend.domain;

import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RecommendUserTest {

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void createUser() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .interestTags(List.of(1L, 2L, 3L))
                .level(Level.JUNIOR)
                .build();

        assertThat(user.getId()).isEqualTo("user-1");
        assertThat(user.getInterestTags()).containsExactly(1L, 2L, 3L);
        assertThat(user.getLevel()).isEqualTo(Level.JUNIOR);
        assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(user.getEnrolledCourseIds()).isEmpty();
        assertThat(user.getCreatedCourseIds()).isEmpty();
    }

    @Test
    @DisplayName("사용자 프로필을 업데이트할 수 있다")
    void updateProfile() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .interestTags(new ArrayList<>(List.of(1L, 2L)))
                .level(Level.JUNIOR)
                .build();

        user.updateProfile(List.of(3L, 4L, 5L), Level.MIDDLE);

        assertThat(user.getInterestTags()).containsExactly(3L, 4L, 5L);
        assertThat(user.getLevel()).isEqualTo(Level.MIDDLE);
    }

    @Test
    @DisplayName("사용자를 비활성화할 수 있다")
    void deactivate() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .build();

        user.deactivate();

        assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("사용자를 활성화할 수 있다")
    void activate() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .status(Status.INACTIVE)
                .build();

        user.activate();

        assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("수강 강의를 추가할 수 있다")
    void addEnrolledCourse() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(new ArrayList<>())
                .build();

        user.addEnrolledCourse("course-1");
        user.addEnrolledCourse("course-2");

        assertThat(user.getEnrolledCourseIds()).containsExactly("course-1", "course-2");
    }

    @Test
    @DisplayName("이미 수강중인 강의는 중복 추가되지 않는다")
    void addEnrolledCourse_noDuplicate() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(new ArrayList<>(List.of("course-1")))
                .build();

        user.addEnrolledCourse("course-1");

        assertThat(user.getEnrolledCourseIds()).containsExactly("course-1");
    }

    @Test
    @DisplayName("수강 강의를 제거할 수 있다")
    void removeEnrolledCourse() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(new ArrayList<>(List.of("course-1", "course-2")))
                .build();

        user.removeEnrolledCourse("course-1");

        assertThat(user.getEnrolledCourseIds()).containsExactly("course-2");
    }

    @Test
    @DisplayName("생성한 강의를 추가할 수 있다")
    void addCreatedCourse() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .createdCourseIds(new ArrayList<>())
                .build();

        user.addCreatedCourse("course-1");

        assertThat(user.getCreatedCourseIds()).containsExactly("course-1");
    }

    @Test
    @DisplayName("생성한 강의를 제거할 수 있다")
    void removeCreatedCourse() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .createdCourseIds(new ArrayList<>(List.of("course-1", "course-2")))
                .build();

        user.removeCreatedCourse("course-1");

        assertThat(user.getCreatedCourseIds()).containsExactly("course-2");
    }
}
