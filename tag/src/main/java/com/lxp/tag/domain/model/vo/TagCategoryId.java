package com.lxp.tag.domain.model.vo;

public record TagCategoryId(Long value) {

    public TagCategoryId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("value must be positive when assigned");
        }
    }
}
