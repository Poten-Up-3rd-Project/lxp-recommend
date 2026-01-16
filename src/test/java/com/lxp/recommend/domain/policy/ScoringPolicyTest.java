package com.lxp.recommend.domain.policy;

import com.lxp.recommend.domain.model.TagContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ScoringPolicy 단위 테스트
 * 태그 매칭 점수 계산 검증
 */
class ScoringPolicyTest {

    @Test
    @DisplayName("Explicit 태그만 매칭 시 기본 가중치 적용")
    void calculateScore_withExplicitTagsOnly() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();  // explicit=1.0, implicit=1.5
        TagContext tagContext = TagContext.create(Set.of("Spring", "DDD"));  // 모두 Explicit
        Set<String> courseTags = Set.of("Spring", "Java");  // Spring 매칭

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then
        assertThat(score).isEqualTo(1.0);  // Spring 1개 매칭 → 1.0점
    }

    @Test
    @DisplayName("Explicit + Implicit 태그 혼합 매칭")
    void calculateScore_withMixedTags() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        Set<String> explicitTags = Set.of("Spring");
        Set<String> implicitTags = Set.of("Docker");  // 학습 이력 기반
        TagContext tagContext = new TagContext(explicitTags, implicitTags);

        Set<String> courseTags = Set.of("Spring", "Docker");

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then
        assertThat(score).isEqualTo(2.5);  // Spring(1.0) + Docker(1.5) = 2.5
    }

    @Test
    @DisplayName("매칭되는 태그가 없으면 0점")
    void calculateScore_noMatch() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = TagContext.create(Set.of("Spring", "DDD"));
        Set<String> courseTags = Set.of("React", "TypeScript");

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    @DisplayName("강좌 태그가 비어있으면 0점")
    void calculateScore_emptyCourseTags() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = TagContext.create(Set.of("Spring"));
        Set<String> courseTags = Set.of();

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then
        assertThat(score).isEqualTo(0.0);
    }
}
