package com.lxp.recommend.infrastructure.external.course;

import com.lxp.recommend.domain.model.ids.DifficultyLevel;
import java.util.Set;

public record CourseMetaView(
        String courseId,          // UUID -> Long
        Set<String> tags,
        DifficultyLevel difficulty,
        boolean isPublic
) {}