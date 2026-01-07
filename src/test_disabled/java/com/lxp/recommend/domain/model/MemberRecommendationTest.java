package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.exception.DuplicateCourseException;
import com.lxp.recommend.domain.exception.RecommendLimitExceededException;
import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.model.ids.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemberRecommendationTest {

    private MemberId memberId;

    @BeforeEach
    void setUp() {
        memberId = MemberId.of("test-member-001");
    }

    @Nested
    @DisplayName("MemberRecommendation 생성")
    class CreateRecommendation {

        @Test
        @DisplayName("정상 생성")
        void create() {
            // When
            MemberRecommendation recommendation = new MemberRecommendation(memberId);

            // Then
            assertThat(recommendation.getMemberId()).isEqualTo(memberId);
            assertThat(recommendation.getItems()).isEmpty();
            assertThat(recommendation.getCalculatedAt()).isNotNull();
        }

        @Test
        @DisplayName("memberId가 null이면 예외 발생")
        void createWithNullMemberId() {
            assertThatThrownBy(() -> new MemberRecommendation(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("memberId는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("추천 아이템 업데이트")
    class UpdateItems {

        private MemberRecommendation recommendation;

        @BeforeEach
        void setUp() {
            recommendation = new MemberRecommendation(memberId);
        }

        @Test
        @DisplayName("정상적으로 아이템 업데이트")
        void updateItems() {
            // Given
            List<RecommendedCourse> items = List.of(
                    new RecommendedCourse(CourseId.of("course-1"), 10.0, 1),
                    new RecommendedCourse(CourseId.of("course-2"), 8.5, 2),
                    new RecommendedCourse(CourseId.of("course-3"), 7.0, 3)
            );

            // When
            recommendation.updateItems(items);

            // Then
            assertThat(recommendation.getItems()).hasSize(3);
            assertThat(recommendation.getItems().get(0).getCourseId().getValue()).isEqualTo("course-1");
            assertThat(recommendation.getCalculatedAt()).isNotNull();
        }

        @Test
        @DisplayName("10개 제한 검증 - 11개 추가 시 예외 발생")
        void updateItemsExceedsLimit() {
            // Given: 11개 아이템
            List<RecommendedCourse> items = new ArrayList<>();
            for (int i = 1; i <= 11; i++) {
                items.add(new RecommendedCourse(
                        CourseId.of("course-" + i),
                        10.0 - i,
                        i
                ));
            }

            // When & Then
            assertThatThrownBy(() -> recommendation.updateItems(items))
                    .isInstanceOf(RecommendLimitExceededException.class)
                    .hasMessageContaining("최대 10개까지 가능합니다");
        }

        @Test
        @DisplayName("중복 강좌 검증 - 중복 포함 시 예외 발생")
        void updateItemsWithDuplicates() {
            // Given: course-1이 중복
            List<RecommendedCourse> items = List.of(
                    new RecommendedCourse(CourseId.of("course-1"), 10.0, 1),
                    new RecommendedCourse(CourseId.of("course-2"), 9.0, 2),
                    new RecommendedCourse(CourseId.of("course-1"), 8.0, 3) // 중복!
            );

            // When & Then
            assertThatThrownBy(() -> recommendation.updateItems(items))
                    .isInstanceOf(DuplicateCourseException.class)
                    .hasMessageContaining("중복된 강좌");
        }

        @Test
        @DisplayName("순위 연속성 검증 - 순위가 건너뛰면 예외 발생")
        void updateItemsRankContinuity() {
            // Given: 순위가 1, 2, 4 (3이 빠짐)
            List<RecommendedCourse> items = List.of(
                    new RecommendedCourse(CourseId.of("course-1"), 10.0, 1),
                    new RecommendedCourse(CourseId.of("course-2"), 9.0, 2),
                    new RecommendedCourse(CourseId.of("course-3"), 8.0, 4) // 4로 건너뜀!
            );

            // When & Then
            assertThatThrownBy(() -> recommendation.updateItems(items))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("순위가 연속적이지 않습니다");
        }

        @Test
        @DisplayName("null 아이템 리스트 전달 시 예외 발생")
        void updateItemsWithNull() {
            assertThatThrownBy(() -> recommendation.updateItems(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("items는 null일 수 없습니다");
        }

        @Test
        @DisplayName("빈 리스트로 업데이트 가능")
        void updateItemsWithEmptyList() {
            // When
            recommendation.updateItems(List.of());

            // Then
            assertThat(recommendation.getItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("추천 재구성 (JPA → Domain 변환)")
    class ReconstructRecommendation {

        @Test
        @DisplayName("정상적으로 재구성")
        void reconstruct() {
            // Given
            List<RecommendedCourse> items = List.of(
                    new RecommendedCourse(CourseId.of("course-1"), 10.0, 1)
            );

            // When
            MemberRecommendation recommendation = MemberRecommendation.reconstruct(
                    1L,
                    memberId,
                    items,
                    java.time.LocalDateTime.now()
            );

            // Then
            assertThat(recommendation.getId()).isEqualTo(1L);
            assertThat(recommendation.getMemberId()).isEqualTo(memberId);
            assertThat(recommendation.getItems()).hasSize(1);
        }
    }
}
