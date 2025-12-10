package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.dto.DifficultyLevel;
import com.lxp.recommend.domain.model.ids.CourseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CourseCandidateTest {

    @Test
    @DisplayName("정상 생성")
    void create() {
        // When
        CourseCandidate candidate = new CourseCandidate(
                CourseId.of("course-1"),
                Set.of("Java", "Spring"),
                DifficultyLevel.JUNIOR,
                true
        );

        // Then
        assertThat(candidate.courseId().getValue()).isEqualTo("course-1");
        assertThat(candidate.tags()).containsExactlyInAnyOrder("Java", "Spring");
        assertThat(candidate.difficulty()).isEqualTo(DifficultyLevel.JUNIOR);
        assertThat(candidate.isPublic()).isTrue();
    }

    @Test
    @DisplayName("courseId가 null이면 예외 발생")
    void createWithNullCourseId() {
        assertThatThrownBy(() -> new CourseCandidate(
                null,
                Set.of("Java"),
                DifficultyLevel.JUNIOR,
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("courseId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("difficulty가 null이면 예외 발생")
    void createWithNullDifficulty() {
        assertThatThrownBy(() -> new CourseCandidate(
                CourseId.of("course-1"),
                Set.of("Java"),
                null,
                true
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("difficulty는 null일 수 없습니다");
    }

    @Test
    @DisplayName("tags가 null이면 빈 Set으로 처리")
    void createWithNullTags() {
        // When
        CourseCandidate candidate = new CourseCandidate(
                CourseId.of("course-1"),
                null,
                DifficultyLevel.JUNIOR,
                true
        );

        // Then
        assertThat(candidate.tags()).isEmpty();
    }

    @Test
    @DisplayName("matchesDifficulty - 타겟 난이도에 포함되면 true")
    void matchesDifficulty() {
        // Given
        CourseCandidate candidate = new CourseCandidate(
                CourseId.of("course-1"),
                Set.of("Java"),
                DifficultyLevel.MIDDLE,
                true
        );

        // When & Then
        assertThat(candidate.matchesDifficulty(Set.of(DifficultyLevel.JUNIOR, DifficultyLevel.MIDDLE))).isTrue();
        assertThat(candidate.matchesDifficulty(Set.of(DifficultyLevel.JUNIOR, DifficultyLevel.SENIOR))).isFalse();
    }

    @Test
    @DisplayName("hasTags - 태그가 있으면 true")
    void hasTags() {
        // Given
        CourseCandidate candidate1 = new CourseCandidate(
                CourseId.of("course-1"),
                Set.of("Java"),
                DifficultyLevel.JUNIOR,
                true
        );
        CourseCandidate candidate2 = new CourseCandidate(
                CourseId.of("course-2"),
                Set.of(),
                DifficultyLevel.JUNIOR,
                true
        );

        // Then
        assertThat(candidate1.hasTags()).isTrue();
        assertThat(candidate2.hasTags()).isFalse();
    }

    @Test
    @DisplayName("hasTag - 특정 태그 포함 여부 확인")
    void hasTag() {
        // Given
        CourseCandidate candidate = new CourseCandidate(
                CourseId.of("course-1"),
                Set.of("Java", "Spring"),
                DifficultyLevel.JUNIOR,
                true
        );

        // Then
        assertThat(candidate.hasTag("Java")).isTrue();
        assertThat(candidate.hasTag("React")).isFalse();
    }
}
