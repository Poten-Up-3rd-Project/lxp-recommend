package com.lxp.enrollment.domain.event;

import com.lxp.common.domain.event.DomainEvent;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;

import java.time.LocalDateTime;

public record ProgressCreatedEvent(UserId userId, CourseId courseId) implements DomainEvent  {

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
        return null;
    }

    @Override
    public String getAggregateId() {
        return "";
    }

    public static ProgressCreatedEvent of(UserId userId, CourseId courseId) {
        return new ProgressCreatedEvent(userId, courseId);
    }
}
