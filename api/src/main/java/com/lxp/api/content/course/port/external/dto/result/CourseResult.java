package com.lxp.api.content.course.port.external.dto.result;

import com.lxp.common.enums.Level;

import java.util.List;

public record CourseResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        Level difficulty, // JUNIOR, MIDDLE, SENIOR, EXPERT
        List<Long> tags
) {
}
