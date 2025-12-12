package com.lxp.enrollment.infrastructure.persistence.repository;

import com.lxp.enrollment.infrastructure.persistence.entity.CourseProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCourseProgressRepository extends JpaRepository<CourseProgressEntity, Long> {

}
