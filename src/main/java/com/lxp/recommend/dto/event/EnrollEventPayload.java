package com.lxp.recommend.dto.event;

import java.time.LocalDateTime;

public record EnrollEventPayload(
        String eventType,
        String userId,
        String courseId,
        LocalDateTime timestamp
) {
    public boolean isCreated() {
        return "ENROLL_CREATED".equals(eventType);
    }

    public boolean isDeleted() {
        return "ENROLL_DELETED".equals(eventType);
    }
}
