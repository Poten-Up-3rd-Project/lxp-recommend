package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.model.ids.EnrollmentStatus;
import com.lxp.recommend.domain.model.ids.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendContextTest {

    private final CourseId javaBasicId = CourseId.of("java-basic");
    private final CourseId springBootId = CourseId.of("spring-boot");

    // --- [Rule 1] 필터링 로직 (제외 처리) ---

    @Test
    @DisplayName("[Case 1] 수강 중(ENROLLED)인 강좌는 추천 후보에서 제외된다")
    void excludeEnrolledCourse() {
        // Given
        LearningHistory history = new LearningHistory(javaBasicId, EnrollmentStatus.ENROLLED);
        CourseCandidate candidate = new CourseCandidate(javaBasicId, Set.of("Java"), Level.JUNIOR, true);

        // When
        RecommendContext context = RecommendContext.create(Set.of(), List.of(history), List.of(candidate));

        // Then
        assertThat(context.getFilteredCandidates()).isEmpty();
        assertThat(context.getExcludedCourseIds()).contains(javaBasicId);
    }

    @Test
    @DisplayName("[Case 2] 수강 완료(COMPLETED)된 강좌는 추천 후보에서 제외된다")
    void excludeCompletedCourse() {
        // Given
        LearningHistory history = new LearningHistory(javaBasicId, EnrollmentStatus.COMPLETED);
        CourseCandidate candidate = new CourseCandidate(javaBasicId, Set.of("Java"), Level.JUNIOR, true);

        // When
        RecommendContext context = RecommendContext.create(Set.of(), List.of(history), List.of(candidate));

        // Then
        assertThat(context.getFilteredCandidates()).isEmpty();
    }

    @Test
    @DisplayName("[Case 3] 수강 취소(CANCELLED)된 강좌는 다시 추천될 수 있다")
    void includeCancelledCourse() {
        // Given
        LearningHistory history = new LearningHistory(javaBasicId, EnrollmentStatus.CANCELLED);
        CourseCandidate candidate = new CourseCandidate(javaBasicId, Set.of("Java"), Level.JUNIOR, true);

        // When
        RecommendContext context = RecommendContext.create(Set.of(), List.of(history), List.of(candidate));

        // Then
        assertThat(context.getFilteredCandidates()).hasSize(1);
    }

    // --- [Rule 2] Implicit 태그 추출 ---

    @Test
    @DisplayName("[Case 4] 수강 중인 강좌의 태그는 Implicit(암묵적) 태그로 추출된다")
    void extractImplicitTagsFromEnrolled() {
        // Given
        LearningHistory history = new LearningHistory(javaBasicId, EnrollmentStatus.ENROLLED);
        // 후보군 데이터에 해당 강좌의 태그 정보가 있어야 함
        CourseCandidate candidate = new CourseCandidate(javaBasicId, Set.of("Java", "Basic"), Level.JUNIOR, true);

        // When
        RecommendContext context = RecommendContext.create(Set.of(), List.of(history), List.of(candidate));

        // Then
        assertThat(context.getTagContext().implicitTags())
                .containsExactlyInAnyOrder("Java", "Basic");
    }

    @Test
    @DisplayName("[Case 5] 수강 완료된 강좌의 태그는 Implicit 태그에 포함되지 않는다 (이미 학습함)")
    void doNotExtractImplicitTagsFromCompleted() {
        // Given
        LearningHistory history = new LearningHistory(javaBasicId, EnrollmentStatus.COMPLETED);
        CourseCandidate candidate = new CourseCandidate(javaBasicId, Set.of("Java"), Level.JUNIOR, true);

        // When
        RecommendContext context = RecommendContext.create(Set.of(), List.of(history), List.of(candidate));

        // Then
        assertThat(context.getTagContext().implicitTags()).isEmpty();
    }

    // --- [Rule 3] 컨텍스트 유효성 ---

    @Test
    @DisplayName("[Case 6] 태그도 없고 후보군도 없으면 유효하지 않은 컨텍스트")
    void invalidContext_whenEmpty() {
        RecommendContext context = RecommendContext.create(Set.of(), List.of(), List.of());
        assertThat(context.hasValidContext()).isFalse();
    }

    @Test
    @DisplayName("[Case 7] Explicit 태그만 있어도 유효한 컨텍스트다")
    void validContext_withExplicitTags() {
        CourseCandidate candidate = new CourseCandidate(springBootId, Set.of("Spring"), Level.MIDDLE, true);
        RecommendContext context = RecommendContext.create(Set.of("Java"), List.of(), List.of(candidate));

        assertThat(context.hasValidContext()).isTrue();
    }
}