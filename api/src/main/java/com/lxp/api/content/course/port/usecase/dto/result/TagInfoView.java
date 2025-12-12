package com.lxp.api.content.course.port.usecase.dto.result;

public record TagInfoView(
        Long id,
        String content,
        String color,
        String variant
) {
}