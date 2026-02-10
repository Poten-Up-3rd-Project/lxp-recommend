package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseJpaRepository extends JpaRepository<RecommendCourse, String> {

    List<RecommendCourse> findByStatus(Status status);

    List<RecommendCourse> findByInstructorId(String instructorId);

    long countByStatus(Status status);
}
