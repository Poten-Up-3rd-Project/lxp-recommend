package com.lxp.content.course.interfaces.dto.response;

public record TagResponse(
    Long id,
    String content,
    String color,
    String variant
) {
}
