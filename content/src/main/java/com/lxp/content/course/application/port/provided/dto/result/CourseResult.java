package com.lxp.content.course.application.port.provided.dto.result;

import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import java.util.List;
import java.util.Map;

public record CourseResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        CourseDifficulty difficulty, // JUNIOR, MIDDLE, SENIOR, EXPERT
        List<Long> tags
) {
}
