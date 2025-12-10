package com.lxp.content.course.infrastructure.persistence.repository;

import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.infrastructure.persistence.entity.CourseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, Long> {
    Optional<CourseJpaEntity> findByUuid(String uuid);
    List<CourseJpaEntity> findAllByUuidIn(Collection<String> uuids);
}
