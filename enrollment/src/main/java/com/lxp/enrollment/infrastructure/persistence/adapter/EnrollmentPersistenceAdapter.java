package com.lxp.enrollment.infrastructure.persistence.adapter;

import com.lxp.enrollment.domain.exception.EnrollmentErrorCode;
import com.lxp.enrollment.domain.exception.EnrollmentException;
import com.lxp.enrollment.domain.model.Enrollment;
import com.lxp.enrollment.domain.repository.EnrollmentRepository;
import com.lxp.enrollment.infrastructure.persistence.entity.EnrollmentJpaEntity;
import com.lxp.enrollment.infrastructure.persistence.repository.EnrollmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentPersistenceAdapter implements EnrollmentRepository {
    private final EnrollmentJpaRepository enrollmentJpaRepository;

    @Override
    public Enrollment findById(long enrollmentId) {
        return enrollmentJpaRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentException(EnrollmentErrorCode.NOT_FOUND_ENROLLMENT))
                .toDomain();
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        EnrollmentJpaEntity entity = EnrollmentJpaEntity.from(enrollment);
        enrollmentJpaRepository.save(entity);
        return entity.toDomain();
    }
}
