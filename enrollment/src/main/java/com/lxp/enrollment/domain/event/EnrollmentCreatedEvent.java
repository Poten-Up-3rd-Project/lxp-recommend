package com.lxp.enrollment.domain.event;

import com.lxp.common.domain.event.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentCreatedEvent extends BaseDomainEvent {
    private final String courseUUID;
    private final String userUUID;

    public EnrollmentCreatedEvent(String enrollmentUuid, String courseId, String userId) {
        super(enrollmentUuid);

        this.courseUUID = courseId;
        this.userUUID = userId;
    }

    public EnrollmentCreatedEvent(String eventId, LocalDateTime occurredAt, String enrollmentUuid, String courseId, String userId) {
        super(eventId, enrollmentUuid, occurredAt);
        this.courseUUID = courseId;
        this.userUUID = userId;
    }
}
