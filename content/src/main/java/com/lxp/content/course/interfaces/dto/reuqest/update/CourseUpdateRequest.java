package com.lxp.content.course.interfaces.dto.reuqest.update;

import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;

public record CourseUpdateRequest(
        String title,
        String description,
        String thumbnailUrl,
        CourseDifficulty level,
        List<Long> tags
) {
}
