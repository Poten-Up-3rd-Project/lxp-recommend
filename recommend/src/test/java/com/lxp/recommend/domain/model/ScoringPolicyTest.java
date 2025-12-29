package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.policy.ScoringPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ScoringPolicyTest {

    @Test
    @DisplayName("정상 생성")
    void create() {
        // When
        ScoringPolicy policy = new ScoringPolicy(1.0, 1.5);

        // Then
        assertThat(policy.explicitTagWeight()).isEqualTo(1.0);
        assertThat(policy.implicitTagWeight()).isEqualTo(1.5);
    }

    @Test
    @DisplayName("가중치가 음수이면 예외 발생")
    void createWithNegativeWeight() {
        assertThatThrownBy(() -> new ScoringPolicy(-1.0, 1.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가중치는 0 이상이어야 합니다");
    }

    @Test
    @DisplayName("defaultPolicy - 기본 정책")
    void defaultPolicy() {
        // When
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();

        // Then
        assertThat(policy.explicitTagWeight()).isEqualTo(1.0);
        assertThat(policy.implicitTagWeight()).isEqualTo(1.5);
    }

    @Test
    @DisplayName("calculateScore - Explicit 태그만 매칭")
    void calculateScoreExplicitOnly() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = new TagContext(Set.of("Java", "Spring"), Set.of());
        Set<String> courseTags = Set.of("Java", "Docker");

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then: Java 태그만 매칭 (Explicit 1.0점)
        assertThat(score).isEqualTo(1.0);
    }

    @Test
    @DisplayName("calculateScore - Implicit 태그 우선 적용")
    void calculateScoreImplicitPriority() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = new TagContext(
                Set.of("Java", "Spring"),
                Set.of("Spring", "JPA") // Spring이 Implicit에도 있음
        );
        Set<String> courseTags = Set.of("Spring", "JPA");

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then: Spring(Implicit 1.5) + JPA(Implicit 1.5) = 3.0
        assertThat(score).isEqualTo(3.0);
    }

    @Test
    @DisplayName("calculateScore - 매칭 태그 없으면 0점")
    void calculateScoreNoMatch() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = new TagContext(Set.of("Java"), Set.of("Spring"));
        Set<String> courseTags = Set.of("React", "Vue");

        // When
        double score = policy.calculateScore(courseTags, tagContext);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    @DisplayName("calculateScore - 강좌 태그가 null이면 0점")
    void calculateScoreNullCourseTags() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = new TagContext(Set.of("Java"), Set.of());

        // When
        double score = policy.calculateScore(null, tagContext);

        // Then
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    @DisplayName("calculateScore - 강좌 태그가 비어있으면 0점")
    void calculateScoreEmptyCourseTags() {
        // Given
        ScoringPolicy policy = ScoringPolicy.defaultPolicy();
        TagContext tagContext = new TagContext(Set.of("Java"), Set.of());

        // When
        double score = policy.calculateScore(Set.of(), tagContext);

        // Then
        assertThat(score).isEqualTo(0.0);
    }
}
