package com.lxp.tag.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.tag.domain.exception.TagErrorCode;
import com.lxp.tag.domain.exception.TagException;
import com.lxp.tag.domain.model.enums.TagState;
import com.lxp.tag.domain.model.vo.TagCategoryId;
import com.lxp.tag.domain.model.vo.TagId;

import java.util.Objects;

public class Tag extends AggregateRoot<TagId> {
    private TagId tagId;
    private TagCategoryId tagCategoryId;
    private String name;
    private TagState state;

    private Tag() {}

    @Override
    public TagId getId() {
        return this.tagId;
    }

    private Tag(TagCategoryId tagCategoryId, String name, TagState state) {
        this.tagCategoryId = tagCategoryId;
        this.name = name;
        this.state = state;
    }

    public static Tag create(TagCategoryId tagCategoryId, String name) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(tagCategoryId, "tagCategoryId must not be null");
        return new Tag(tagCategoryId, name, TagState.ACTIVE);
    }

    public void changeCategory(TagCategoryId tagCategoryId) {
        if (this.state == TagState.INACTIVE) {
            throw new TagException(TagErrorCode.INVALID_CHANGE_CATEGORY);
        }
        this.tagCategoryId = tagCategoryId;
    }

    public void rename(String newName) {
        Objects.requireNonNull(newName, "newName must not be null");
        this.name = newName;
    }

    public void deactivate() {
        this.state = TagState.INACTIVE;
    }

    public void activate() {
        this.state = TagState.ACTIVE;
    }

    public TagCategoryId tagCategoryId() {
        return this.tagCategoryId;
    }

    public String name() {
        return this.name;
    }

    public boolean isActive() {
        return this.state == TagState.ACTIVE;
    }
}
