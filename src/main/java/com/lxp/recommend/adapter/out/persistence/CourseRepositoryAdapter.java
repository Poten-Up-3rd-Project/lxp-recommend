package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.application.port.out.CourseRepository;
import com.lxp.recommend.domain.user.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryAdapter implements CourseRepository {

    private final CourseJpaRepository courseJpaRepository;

    @Override
    public RecommendCourse save(RecommendCourse course) {
        return courseJpaRepository.save(course);
    }

    @Override
    public Optional<RecommendCourse> findById(String id) {
        return courseJpaRepository.findById(id);
    }

    @Override
    public List<RecommendCourse> findByStatus(Status status) {
        return courseJpaRepository.findByStatus(status);
    }

    @Override
    public List<RecommendCourse> findByInstructorId(String instructorId) {
        return courseJpaRepository.findByInstructorId(instructorId);
    }

    @Override
    public long countByStatus(Status status) {
        return courseJpaRepository.countByStatus(status);
    }

    @Override
    public boolean existsById(String id) {
        return courseJpaRepository.existsById(id);
    }
}
