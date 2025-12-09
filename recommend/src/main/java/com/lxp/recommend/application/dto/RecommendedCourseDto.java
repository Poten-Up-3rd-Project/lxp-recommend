package com.lxp.recommend.application.dto;

public record RecommendedCourseDto(
        String courseId,
        double score,
        int rank
) {}
