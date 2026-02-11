package com.lxp.recommend.application.port.out;

import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.Status;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    RecommendCourse save(RecommendCourse course);

    Optional<RecommendCourse> findById(String id);

    List<RecommendCourse> findByStatus(Status status);

    List<RecommendCourse> findByInstructorId(String instructorId);

    long countByStatus(Status status);

    boolean existsById(String id);
}
