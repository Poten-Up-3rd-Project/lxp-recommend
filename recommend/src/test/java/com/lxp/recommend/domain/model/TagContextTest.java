package com.lxp.recommend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TagContextTest {

    @Test
    @DisplayName("정상 생성 - Explicit 및 Implicit 태그")
    void create() {
        // Given
        Set<String> explicitTags = Set.of("Java", "Spring");
        Set<String> implicitTags = Set.of("Spring", "JPA");

        // When
        TagContext context = new TagContext(explicitTags, implicitTags);

        // Then
        assertThat(context.explicitTags()).containsExactlyInAnyOrder("Java", "Spring");
        assertThat(context.implicitTags()).containsExactlyInAnyOrder("Spring", "JPA");
    }

    @Test
    @DisplayName("null 방어 - null 전달 시 빈 Set으로 처리")
    void createWithNull() {
        // When
        TagContext context = new TagContext(null, null);

        // Then
        assertThat(context.explicitTags()).isEmpty();
        assertThat(context.implicitTags()).isEmpty();
    }

    @Test
    @DisplayName("hasAnyTag - 태그가 하나라도 있으면 true")
    void hasAnyTag() {
        // Given
        TagContext context1 = new TagContext(Set.of("Java"), Set.of());
        TagContext context2 = new TagContext(Set.of(), Set.of("Spring"));
        TagContext context3 = new TagContext(Set.of(), Set.of());

        // Then
        assertThat(context1.hasAnyTag()).isTrue();
        assertThat(context2.hasAnyTag()).isTrue();
        assertThat(context3.hasAnyTag()).isFalse();
    }

    @Test
    @DisplayName("isExplicit - Explicit 태그 여부 확인")
    void isExplicit() {
        // Given
        TagContext context = new TagContext(Set.of("Java", "Spring"), Set.of("JPA"));

        // Then
        assertThat(context.isExplicit("Java")).isTrue();
        assertThat(context.isExplicit("JPA")).isFalse();
        assertThat(context.isExplicit("React")).isFalse();
    }

    @Test
    @DisplayName("isImplicit - Implicit 태그 여부 확인")
    void isImplicit() {
        // Given
        TagContext context = new TagContext(Set.of("Java"), Set.of("Spring", "JPA"));

        // Then
        assertThat(context.isImplicit("Spring")).isTrue();
        assertThat(context.isImplicit("Java")).isFalse();
        assertThat(context.isImplicit("React")).isFalse();
    }

    @Test
    @DisplayName("contains - Explicit 또는 Implicit 중 하나라도 포함되면 true")
    void contains() {
        // Given
        TagContext context = new TagContext(Set.of("Java"), Set.of("Spring"));

        // Then
        assertThat(context.contains("Java")).isTrue();
        assertThat(context.contains("Spring")).isTrue();
        assertThat(context.contains("React")).isFalse();
    }

    @Test
    @DisplayName("totalTagCount - 전체 태그 수")
    void totalTagCount() {
        // Given
        TagContext context = new TagContext(Set.of("Java", "Spring"), Set.of("JPA", "Hibernate"));

        // Then
        assertThat(context.totalTagCount()).isEqualTo(4);
    }
}
