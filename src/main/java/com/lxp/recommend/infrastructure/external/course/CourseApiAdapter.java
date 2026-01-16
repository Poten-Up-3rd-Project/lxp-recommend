package com.lxp.recommend.infrastructure.external.course;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;
import com.lxp.recommend.infrastructure.web.internal.client.CourseServiceFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Course BC API 어댑터 (Feign 기반)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourseApiAdapter implements CourseMetaQueryPort {

    private final CourseServiceFeignClient feignClient;

    @Override
    public List<CourseMetaData> findByDifficulties(Set<String> difficulties, int limit) {
        String difficultiesParam = String.join(",", difficulties);

        log.debug("[Course API] Fetching courses: difficulties={}, limit={}",
                difficultiesParam, limit);

        try {
            InternalApiResponse<List<CourseMetaResponse>> response =
                    feignClient.searchCourses(difficultiesParam, limit);

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Course API] Failed or empty response");
                return List.of();
            }

            List<CourseMetaData> courses = response.data().stream()
                    .map(course -> new CourseMetaData(
                            course.courseId(),
                            Set.copyOf(course.tags()),
                            course.difficulty(),
                            course.isPublic()
                    ))
                    .toList();

            log.debug("[Course API] Successfully fetched {} courses", courses.size());
            return courses;

        } catch (Exception e) {
            log.error("[Course API] Error fetching courses: difficulties={}", difficultiesParam, e);
            return List.of();
        }
    }

    @Override
    public Optional<CourseMetaData> findById(String courseId) {
        log.debug("[Course API] Fetching course by id: {}", courseId);

        try {
            InternalApiResponse<CourseMetaResponse> response = feignClient.getCourseById(courseId);

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Course API] Course not found: {}", courseId);
                return Optional.empty();
            }

            CourseMetaResponse course = response.data();
            CourseMetaData courseData = new CourseMetaData(
                    course.courseId(),
                    Set.copyOf(course.tags()),
                    course.difficulty(),
                    course.isPublic()
            );

            log.debug("[Course API] Successfully fetched course: {}", courseId);
            return Optional.of(courseData);

        } catch (Exception e) {
            log.error("[Course API] Error fetching course: {}", courseId, e);
            return Optional.empty();
        }
    }
}
