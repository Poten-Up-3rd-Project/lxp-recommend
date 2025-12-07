package com.lxp.content.course.domain.model.id;

public record SectionUUID(String value) {
    public SectionUUID {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SectionId must be not null");
        }
    }
}
