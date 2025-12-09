package com.lxp.tag.presentation.web.dto;

import com.lxp.tag.application.port.query.CategoryResult;

import java.util.List;

public record TagCategoryResponse(
        long tagCategoryId,
        String name,
        String state,
        List<TagResponse> tags
) {
    public static TagCategoryResponse from(CategoryResult result) {
        return new TagCategoryResponse(
                result.tagCategoryId(),
                result.name(),
                result.state(),
                result.tags().stream().map(TagResponse::from).toList()
        );
    }
}
