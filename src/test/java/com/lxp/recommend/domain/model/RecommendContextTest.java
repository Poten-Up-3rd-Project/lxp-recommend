package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.EnrollmentStatus;
import com.lxp.recommend.infrastructure.external.enrollment.LearningStatusView;
import com.lxp.recommend.domain.model.ids.CourseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class RecommendContextTest {

    @Test
    @DisplayName("정상 생성 및 필터링")
    void create() {
        // Given
        Set<String> explicitTags = Set.of("Java", "Spring");

        List<LearningStatusView> histories = List.of(
                new LearningStatusView("member-1", "course-1", EnrollmentStatus.ENROLLED),
                new LearningStatusView("member-1", "course-2", EnrollmentStatus.COMPLETED)
        );

        List<CourseCandidate> candidates = List.of(
                new CourseCandidate(CourseId.of("course-1"), Set.of("Java"), Level.JUNIOR, true),
                new CourseCandidate(CourseId.of("course-2"), Set.of("Spring"), Level.MIDDLE, true),
                new CourseCandidate(CourseId.of("course-3"), Set.of("JPA"), Level.JUNIOR, true)
        );

        // When
        RecommendContext context = RecommendContext.create(explicitTags, histories, candidates);

        // Then
        assertThat(context.getFilteredCandidates()).hasSize(1); // course-3만 남음
        assertThat(context.getFilteredCandidates().get(0).courseId().getValue()).isEqualTo("course-3");
        assertThat(context.getExcludedCourseIds()).containsExactlyInAnyOrder(
                CourseId.of("course-1"),
                CourseId.of("course-2")
        );
    }

    @Test
    @DisplayName("제외 강좌 계산 - ENROLLED와 COMPLETED 강좌 제외")
    void calculateExcludedCourses() {
        // Given
        List<LearningStatusView> histories = List.of(
                new LearningStatusView("member-1", "course-1", EnrollmentStatus.ENROLLED),
                new LearningStatusView("member-1", "course-2", EnrollmentStatus.COMPLETED),
                new LearningStatusView("member-1", "course-3", EnrollmentStatus.CANCELLED) // 제외 안 됨
        );

        List<CourseCandidate> candidates = List.of(
                new CourseCandidate(CourseId.of("course-1"), Set.of("Java"), Level.JUNIOR, true),
                new CourseCandidate(CourseId.of("course-2"), Set.of("Spring"), Level.JUNIOR, true),
                new CourseCandidate(CourseId.of("course-3"), Set.of("JPA"), Level.JUNIOR, true),
                new CourseCandidate(CourseId.of("course-4"), Set.of("Docker"), Level.JUNIOR, true)
        );

        // When
        RecommendContext context = RecommendContext.create(Set.of("Java"), histories, candidates);

        // Then
        assertThat(context.getExcludedCourseIds()).containsExactlyInAnyOrder(
                CourseId.of("course-1"),
                CourseId.of("course-2")
        );
        assertThat(context.getFilteredCandidates()).hasSize(2); // course-3, course-4만 남음
    }

    @Test
    @DisplayName("Implicit 태그 추출 - 수강 중인 강좌의 태그")
    void extractImplicitTags() {
        // Given
        List<LearningStatusView> histories = List.of(
                new LearningStatusView("member-1", "course-1", EnrollmentStatus.ENROLLED) // 수강 중
        );

        List<CourseCandidate> candidates = List.of(
                new CourseCandidate(CourseId.of("course-1"), Set.of("Java", "Spring"), Level.JUNIOR, true),
                new CourseCandidate(CourseId.of("course-2"), Set.of("JPA"), Level.JUNIOR, true)
        );

        // When
        RecommendContext context = RecommendContext.create(Set.of(), histories, candidates);

        // Then
        TagContext tagContext = context.getTagContext();
        assertThat(tagContext.implicitTags()).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    @DisplayName("hasValidContext - 태그와 후보가 있으면 true")
    void hasValidContext() {
        // Given
        RecommendContext context1 = RecommendContext.create(
                Set.of("Java"),
                List.of(),
                List.of(new CourseCandidate(CourseId.of("course-1"), Set.of("Java"), Level.JUNIOR, true))
        );

        RecommendContext context2 = RecommendContext.create(
                Set.of(),
                List.of(),
                List.of()
        );

        // Then
        assertThat(context1.hasValidContext()).isTrue();
        assertThat(context2.hasValidContext()).isFalse();
    }

    @Test
    @DisplayName("null 방어 - null 전달 시 빈 컬렉션으로 처리")
    void createWithNull() {
        // When
        RecommendContext context = RecommendContext.create(null, null, null);

        // Then
        assertThat(context.getFilteredCandidates()).isEmpty();
        assertThat(context.hasValidContext()).isFalse();
    }
}
