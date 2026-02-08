package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.CourseSyncCommand;
import com.lxp.recommend.application.port.in.CourseSyncUseCase;
import com.lxp.recommend.application.port.out.CourseRepository;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseSyncService implements CourseSyncUseCase {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public void createCourse(CourseSyncCommand command) {
        if (courseRepository.existsById(command.courseId())) {
            log.warn("Course already exists: {}", command.courseId());
            throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS);
        }

        RecommendCourse course = RecommendCourse.builder()
                .id(command.courseId())
                .tags(command.tags() != null ? command.tags() : new ArrayList<>())
                .level(Level.fromString(command.level()))
                .instructorId(command.instructorId())
                .build();

        courseRepository.save(course);
        log.info("Created course: {}", command.courseId());

        userRepository.findById(command.instructorId())
                .ifPresent(instructor -> {
                    instructor.addCreatedCourse(command.courseId());
                    userRepository.save(instructor);
                    log.info("Added course {} to instructor {}'s created courses", command.courseId(), command.instructorId());
                });
    }

    @Override
    public void deleteCourse(String courseId) {
        RecommendCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        course.deactivate();
        courseRepository.save(course);
        log.info("Deactivated course: {}", courseId);

        userRepository.findById(course.getInstructorId())
                .ifPresent(instructor -> {
                    instructor.removeCreatedCourse(courseId);
                    userRepository.save(instructor);
                    log.info("Removed course {} from instructor {}'s created courses", courseId, course.getInstructorId());
                });
    }
}
