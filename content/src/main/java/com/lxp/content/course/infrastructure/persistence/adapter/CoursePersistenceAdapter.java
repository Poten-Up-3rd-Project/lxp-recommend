package com.lxp.content.course.infrastructure.persistence.adapter;

import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.infrastructure.persistence.entity.CourseJpaEntity;
import com.lxp.content.course.infrastructure.persistence.mapper.CourseEntityMapper;
import com.lxp.content.course.infrastructure.persistence.repository.CourseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements CourseRepository {
    private final CourseJpaRepository jpaRepository;
    private final CourseEntityMapper mapper;

    @Override
    public Course save(Course course) {
        CourseJpaEntity entity = mapper.toEntity(course);
        CourseJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Course> findByUUID(String courseUUID) {
        return jpaRepository.findByUuid(courseUUID)
                .map(mapper::toDomain);
    }

    @Override
    public List<Course> findAllByUUID(Collection<String> courseUUIDs) {
        return jpaRepository.findAllByUuidIn(courseUUIDs).stream().map(mapper::toDomain).toList();
    }
}
