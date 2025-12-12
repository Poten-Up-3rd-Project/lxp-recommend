package com.lxp.enrollment.domain.event;

import com.lxp.common.domain.event.DomainEvent;
import com.lxp.enrollment.domain.model.CourseProgress;

import java.time.LocalDateTime;

/**
 * 강의 진행 완료 이벤트
 */
public record ProgressCompletedEvent(String userId, String courseId) implements DomainEvent {

    @Override
    public String getEventType() {
        return DomainEvent.super.getEventType();
    }

    @Override
    public String getEventId() {
        return "";
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return LocalDateTime.now();
    }

    @Override
    public String getAggregateId() {
        return "";
    }

    public static ProgressCompletedEvent of(CourseProgress courseProgress) {
        return new ProgressCompletedEvent(
                courseProgress.userId().value(),
                courseProgress.courseId().value()
        );
    }
}
