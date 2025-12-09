package com.lxp.tag.application.port.query;

import java.util.List;

public record CategoryResult(
        long tagCategoryId,
        String name,
        String state, // ACTIVE, INACTIVE
        List<TagResult> tags
) {
}
