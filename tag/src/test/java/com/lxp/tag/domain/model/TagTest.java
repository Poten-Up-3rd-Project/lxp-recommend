package com.lxp.tag.domain.model;

import com.lxp.tag.domain.exception.TagErrorCode;
import com.lxp.tag.domain.exception.TagException;
import com.lxp.tag.domain.model.vo.TagCategoryId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    public Tag tag(String name) {
        return Tag.create(new TagCategoryId(1L), "java");
    }

    @Test
    @DisplayName("태그 name으로 Tag를 생성할 수 있다")
    void create_success_withName() {
        // given
        String name = "java";

        // when
        Tag tag = tag(name);

        // then
        assertEquals("java", tag.name());
        assertTrue(tag.isActive());
    }

    @Test
    @DisplayName("태그 name이 null이면 NPE가 발생한다")
    void create_fail_withNullName() {
        // given
        TagCategoryId tagCategoryId = new TagCategoryId(1L);
        String name = null;
        // when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> Tag.create(tagCategoryId, null));

        // then
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("태그의 상태가 INACTIVE이면 카테고리를 변경에 실패한다")
    void changeCategory_fail_withInactiveTag() {
        // given
        Tag tag = tag("java");
        tag.deactivate();

        // when
        TagException exception = assertThrows(TagException.class,
                () -> tag.changeCategory(new TagCategoryId(1L)));

        // then
        assertEquals(TagErrorCode.INVALID_CHANGE_CATEGORY, exception.getErrorCode());
        assertEquals("TAG_001", exception.getErrorCode().getCode());
        assertEquals("Invalid tag can not change category", exception.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("태그 name을 변경할 수 있다")
    void changeName_success_withValidName() {
        // given
        Tag tag = tag("java");

        // when
        tag.rename("language");

        // then
        assertEquals("language", tag.name());
    }
    
    @Test
    @DisplayName("변경 할 name이 null 이면 NPE가 발생한다")
    void changeName_fail_withNullName() {
        // given
        Tag tag = tag("java");
        
        // when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> tag.rename(null));
        
        // then
        assertEquals("newName must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("ACTIVE 태그를 INACTIVE로 변경할 수 있다")
    void changeInactive_success_withActiveState() {
        // given
        Tag tag = tag("java");

        // when
        tag.deactivate();

        // then
        assertFalse(tag.isActive());
    }

    @Test
    @DisplayName("INACTIVE 태그를 ACTIVE로 변경할 수 있다")
    void changeActive_success_withInactiveState() {
        // given
        Tag tag = tag("java");
        tag.deactivate();

        // when
        tag.activate();

        // then
        assertTrue(tag.isActive());
    }

}