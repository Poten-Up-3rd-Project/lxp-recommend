package com.lxp.enrollment.application.service;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.enrollment.application.port.provided.dto.command.CancelEnrollmentCommand;
import com.lxp.enrollment.application.port.provided.dto.result.CancelEnrollmentResult;
import com.lxp.enrollment.application.usecase.CancelEnrollmentUseCase;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.domain.repository.EnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelEnrollmentService implements CancelEnrollmentUseCase {
    private final EnrollmentRepository enrollmentRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CancelEnrollmentResult cancel(CancelEnrollmentCommand command) {
        Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId());
        enrollment.cancel(LocalDateTime.now(), command.reason());

        enrollmentRepository.save(enrollment);

        enrollment.getDomainEvents().forEach(domainEventPublisher::publish);
        enrollment.clearDomainEvents();

        return new CancelEnrollmentResult(
                enrollment.getId().value(),
                enrollment.state().toString(),
                enrollment.enrollmentDate().value()
        );
    }
}
