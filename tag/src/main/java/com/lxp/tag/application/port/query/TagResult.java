package com.lxp.tag.application.port.query;

public record TagResult(
        long tagId,
        String name,
        String state, // ACTIVE, INACTIVE
        String color,
        String variant
) {
}
