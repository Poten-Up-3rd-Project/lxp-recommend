package com.lxp.enrollment.application.service;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.enrollment.application.port.provided.dto.command.EnrollCourseCommand;
import com.lxp.enrollment.application.port.provided.dto.result.EnrollCourseResult;
import com.lxp.enrollment.application.port.required.UserStatusQueryPort;
import com.lxp.enrollment.application.usecase.EnrollCourseUseCase;
import com.lxp.enrollment.domain.exception.EnrollmentErrorCode;
import com.lxp.enrollment.domain.exception.EnrollmentException;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;
import com.lxp.enrollment.domain.repository.EnrollmentRepository;
import com.lxp.enrollment.infrastructure.external.adapter.CourseStatusQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollCourseService implements EnrollCourseUseCase {
    private final EnrollmentRepository enrollmentRepository;
    private final UserStatusQueryPort userStatusQueryPort;
    private final CourseStatusQueryAdapter courseStatusQueryAdapter;
    private final DomainEventPublisher domainEventPublisher;

    public EnrollCourseResult enroll(EnrollCourseCommand command) {
        if (!userStatusQueryPort.isActiveUser(command.userId())) {
            throw new EnrollmentException(EnrollmentErrorCode.INVALID_USER_ENROLL_FAIL);
        }

        if (!courseStatusQueryAdapter.isActiveCourse(command.courseId())) {
            throw new EnrollmentException(EnrollmentErrorCode.INVALID_COURSE_ENROLL_FAIL);
        }

        Enrollment enrollment = Enrollment.create(
                new UserId(command.userId()),
                new CourseId(command.courseId())
        );

        Enrollment saved = enrollmentRepository.save(enrollment);
        enrollment.getDomainEvents().forEach(domainEventPublisher::publish);
        enrollment.clearDomainEvents();

        return new EnrollCourseResult(
                saved.getId().value(),
                saved.state().toString(),
                saved.enrollmentDate().value()
        );
    }
}
