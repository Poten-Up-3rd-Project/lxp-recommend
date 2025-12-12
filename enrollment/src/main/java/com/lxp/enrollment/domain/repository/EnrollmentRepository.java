package com.lxp.enrollment.domain.repository;

import com.lxp.enrollment.domain.model.Enrollment;

public interface EnrollmentRepository {
    Enrollment findById(long enrollmentId);
    Enrollment save(Enrollment enrollment);
}
