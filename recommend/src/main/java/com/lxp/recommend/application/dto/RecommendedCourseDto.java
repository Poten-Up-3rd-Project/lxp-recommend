package com.lxp.recommend.application.dto;

public record RecommendedCourseDto(
        Long courseId,
        double score,
        int rank
) {}
