package com.lxp.tag.domain.model.vo;

public record TagId(Long value) {

    public TagId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("TagId must be positive when assigned");
        }
    }
}
