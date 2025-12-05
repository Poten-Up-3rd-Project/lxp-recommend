package com.lxp.content.course.domain.model.id;

public record TagId(Long value) {
    public TagId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("TagId must be a positive number.");
        }
    }
}
