package com.lxp.recommend.infrastructure.web.dto.request;

import jakarta.annotation.Nullable;

import java.util.List;

public record CourseFilterInternalRequest(
        @Nullable List<String> ids,
        @Nullable List<String> difficulties,
        @Nullable int limit
) {
}
