package com.lxp.recommend.application.dto;

import java.util.UUID;

public record RecommendedCourseDto(
        UUID courseId,
        double score,
        int rank
) {
}