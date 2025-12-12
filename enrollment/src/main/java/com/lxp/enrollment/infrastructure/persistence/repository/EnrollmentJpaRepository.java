package com.lxp.enrollment.infrastructure.persistence.repository;

import com.lxp.enrollment.infrastructure.persistence.entity.EnrollmentJpaEntity;
import com.lxp.enrollment.infrastructure.persistence.enums.EnrollmentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, Long> {
    Page<EnrollmentJpaEntity> findByUserId(String userId, Pageable pageable);

    Page<EnrollmentJpaEntity> findByUserIdAndState(String userId, EnrollmentState state, Pageable pageable);

    Optional<EnrollmentJpaEntity> findById(Long id);
}
