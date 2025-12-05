package com.lxp.content.course.application.port.provided.dto.result;

import java.util.Set;

public record CourseResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        String difficulty, // JUNIOR, MIDDLE, SENIOR, EXPERT
        Set<Long> tagsIds

) {
}
