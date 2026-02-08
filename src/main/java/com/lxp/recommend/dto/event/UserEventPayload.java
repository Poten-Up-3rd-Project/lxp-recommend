package com.lxp.recommend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public record UserEventPayload(
        String eventType,
        String userId,
        List<Long> interestTags,
        String level,
        LocalDateTime timestamp
) {
    public boolean isCreated() {
        return "USER_CREATED".equals(eventType);
    }

    public boolean isUpdated() {
        return "USER_UPDATED".equals(eventType);
    }

    public boolean isDeleted() {
        return "USER_DELETED".equals(eventType);
    }
}
