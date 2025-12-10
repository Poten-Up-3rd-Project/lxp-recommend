package com.lxp.content.course.domain.repository;

import com.lxp.content.course.domain.model.Course;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    Optional<Course> findByUUID(String courseUUID);
    List<Course> findAllByUUID(Collection<String> courseUUIDs);
}
