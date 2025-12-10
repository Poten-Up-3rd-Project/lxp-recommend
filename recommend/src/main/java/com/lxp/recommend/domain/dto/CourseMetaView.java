package com.lxp.recommend.domain.dto;

import java.util.Set;

public record CourseMetaView(
        String courseId,          // UUID -> Long
        Set<String> tags,
        DifficultyLevel difficulty,
        boolean isPublic
) {}