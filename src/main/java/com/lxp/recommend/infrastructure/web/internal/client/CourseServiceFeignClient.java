package com.lxp.recommend.infrastructure.web.internal.client;

import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Course BC Internal API 호출용 FeignClient
 */
@FeignClient(
        name = "course-service",
        url = "${external.course.base-url}"
)
public interface CourseServiceFeignClient {

    @GetMapping("/internal/api-v1/courses/search")
    InternalApiResponse<List<CourseMetaResponse>> searchCourses(
            @RequestParam("difficulties") String difficulties,
            @RequestParam("limit") int limit
    );

    @GetMapping("/internal/api-v1/courses/{courseId}")
    InternalApiResponse<CourseMetaResponse> getCourseById(@PathVariable("courseId") String courseId);
}
