package com.lxp.tag.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.tag.domain.model.enums.TagCategoryState;
import com.lxp.tag.domain.model.vo.TagCategoryId;

import java.util.Objects;

public class TagCategory extends AggregateRoot<TagCategoryId> {
    private TagCategoryId tagCategoryId;
    private String name;
    private TagCategoryState state;

    private TagCategory() {}

    @Override
    public TagCategoryId getId() {
        return this.tagCategoryId;
    }

    public TagCategory(String name, TagCategoryState state) {
        this.name = name;
        this.state = state;
    }

    public static TagCategory create(String name) {
        Objects.requireNonNull(name, "name must not be null");
        return new TagCategory(name, TagCategoryState.ACTIVE);
    }

    public void reconstruct(TagCategoryId tagCategoryId) {
        this.tagCategoryId = tagCategoryId;
    }

    public void rename(String newName) {
        Objects.requireNonNull(newName, "newName must not be null");
        this.name = newName;
    }

    public void deactivate() {
        this.state = TagCategoryState.INACTIVE;
    }

    public void activate() {
        this.state = TagCategoryState.ACTIVE;
    }

    public TagCategoryId id() {
        return tagCategoryId;
    }

    public String name() {
        return this.name;
    }

    public boolean isActive() {
        return this.state == TagCategoryState.ACTIVE;
    }
}
