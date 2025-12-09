package com.lxp.recommend.domain.dto;

import java.util.Set;

public record CourseMetaView(
        String courseId,          // UUID -> Long
        Set<String> tags,
        Set<String> requiredSkills,
        DifficultyLevel difficulty,
        boolean isPublic
) {}