package com.lxp.enrollment.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.enrollment.domain.model.enums.EnrollmentState;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.EnrollmentDate;
import com.lxp.enrollment.domain.model.vo.EnrollmentId;
import com.lxp.enrollment.domain.model.vo.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Enrollment extends AggregateRoot<EnrollmentId> {
    private EnrollmentId enrollmentId;
    private EnrollmentState state;
    private UserId userId;
    private CourseId courseId;
    private EnrollmentDate enrollmentDate;

    private Enrollment() {}

    @Override
    public EnrollmentId getId() {
        return this.enrollmentId;
    }

    private Enrollment(EnrollmentState state, UserId userId, CourseId courseId) {
        this.state = state;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = new EnrollmentDate(LocalDateTime.now());
    }

    public static Enrollment create(UserId userId, CourseId courseId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        Objects.requireNonNull(courseId, "CourseId must not be null");
        return new Enrollment(EnrollmentState.ENROLLED, userId, courseId);
    }

    public void complete() {
        if (this.state == EnrollmentState.CANCELLED) {
            throw new IllegalStateException("취소된 수강은 완료할 수 없습니다.");
        }
        if (this.state == EnrollmentState.COMPLETED) {
            return;
        }

        this.state = EnrollmentState.COMPLETED;
    }

    public void cancel(LocalDateTime now) {
        Objects.requireNonNull(now, "now must not be null");

        if (this.state == EnrollmentState.CANCELLED) {
            return;
        }

        if (this.state == EnrollmentState.COMPLETED) {
            throw new IllegalStateException("완료된 수강은 취소할 수 없습니다.");
        }

        if (!enrollmentDate.canCancel(now)) {
            throw new IllegalStateException("수강 시작 후 3일이 지나 취소할 수 없습니다.");
        }

        this.state = EnrollmentState.CANCELLED;
    }

    public EnrollmentState state() {
        return this.state;
    }

    public EnrollmentDate enrollmentDate() {
        return this.enrollmentDate;
    }
}
