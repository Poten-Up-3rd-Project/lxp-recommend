package com.lxp.tag.presentation.web.dto;

import com.lxp.tag.application.port.query.TagResult;

public record TagResponse(
        long tagId,
        String content,
        String state,
        String color,
        String variant
) {
    public static TagResponse from(TagResult result) {
        return new TagResponse(
                result.tagId(),
                result.name(),
                result.state(),
                result.color(),
                result.variant()
        );
    }
}
