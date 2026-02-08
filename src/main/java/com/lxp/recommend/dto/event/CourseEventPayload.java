package com.lxp.recommend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public record CourseEventPayload(
        String eventType,
        String courseId,
        List<Long> tags,
        String level,
        String instructorId,
        LocalDateTime timestamp
) {
    public boolean isCreated() {
        return "COURSE_CREATED".equals(eventType);
    }

    public boolean isDeleted() {
        return "COURSE_DELETED".equals(eventType);
    }
}
