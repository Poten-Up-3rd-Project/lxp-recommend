package com.lxp.tag.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagCategoryTest {
    @Test
    @DisplayName("태그 카테고리 name으로 TagCategory를 생성할 수 있다")
    void create_success_withName() {
        // given
        String name = "backend";

        // when
        TagCategory tagCategory = TagCategory.create(name);

        // then
        assertEquals("backend", tagCategory.name());
        assertTrue(tagCategory.isActive());
    }

    @Test
    @DisplayName("태그 카테고리 name이 null 이면 NPE가 발생한다")
    void create_fail_withNullName() {
        // given && when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> TagCategory.create(null));

        // then
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("태그 카테고리 name을 변경할 수 있다")
    void changeName_success_withValidName() {
        // given
        TagCategory tagCategory = TagCategory.create("backend");

        // when
        tagCategory.rename("Backend");

        // then
        assertEquals("Backend", tagCategory.name());
    }

    @Test
    @DisplayName("변경 할 name이 null 이면 NPE가 발생한다")
    void changeName_fail_withNullName() {
        // given
        TagCategory tagCategory = TagCategory.create("java");

        // when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> tagCategory.rename(null));

        // then
        assertEquals("newName must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("ACTIVE 태그를 INACTIVE로 변경할 수 있다")
    void changeInactive_success_withActiveState() {
        // given
        TagCategory tagCategory = TagCategory.create("java");

        // when
        tagCategory.deactivate();

        // then
        assertFalse(tagCategory.isActive());
    }

    @Test
    @DisplayName("INACTIVE 태그를 ACTIVE로 변경할 수 있다")
    void changeActive_success_withInactiveState() {
        // given
        TagCategory tagCategory = TagCategory.create("java");
        tagCategory.deactivate();

        // when
        tagCategory.activate();

        // then
        assertTrue(tagCategory.isActive());
    }
}