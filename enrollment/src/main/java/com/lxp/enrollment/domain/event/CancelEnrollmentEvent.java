package com.lxp.enrollment.domain.event;

import com.lxp.common.domain.event.BaseDomainEvent;
import lombok.Getter;

@Getter
public class CancelEnrollmentEvent extends BaseDomainEvent {
    private final String courseUUID;
    private final String userUUID;

    public CancelEnrollmentEvent(String enrollmentUUID, String courseUUID, String userUUID) {
        super(enrollmentUUID);
        this.courseUUID = courseUUID;
        this.userUUID = userUUID;
    }
}
