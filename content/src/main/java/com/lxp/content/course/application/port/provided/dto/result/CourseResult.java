package com.lxp.content.course.application.port.provided.dto.result;

import java.util.List;
import java.util.Map;

public record CourseResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        String difficulty, // JUNIOR, MIDDLE, SENIOR, EXPERT
        Map<Long,Integer> tags

) {
}
