package com.lxp.api.content.course.port.usecase.dto.result;

import com.lxp.common.enums.Level;

import java.time.Instant;

public record CourseView(
        String courseId,
        String title,
        String description,
        InstructorView InstructorName,
        String thumbnailUrl,
        Level level,
        Instant createdAt,
        Instant updatedAt
) {
}
