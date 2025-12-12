package com.lxp.enrollment.domain.event;

import com.lxp.common.domain.event.DomainEvent;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;

import java.time.LocalDateTime;

/**
 * 강좌 진행률 삭제 이벤트
 * @param userId 사용자 ID
 * @param courseId 강좌 ID
 */
public record ProgressDeletedEvent(UserId userId, CourseId courseId) implements DomainEvent {

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

    public static ProgressDeletedEvent of(UserId userId, CourseId courseId) {
        return new ProgressDeletedEvent(userId, courseId);
    }

}
