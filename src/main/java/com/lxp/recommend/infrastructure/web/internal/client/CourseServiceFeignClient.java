package com.lxp.recommend.infrastructure.web.internal.client;

import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;
import com.lxp.recommend.infrastructure.web.dto.request.CourseFilterInternalRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course BC Internal API 호출용 FeignClient
 */
@FeignClient(
        name = "course-service",
        url = "${external.course.base-url}"
)
public interface CourseServiceFeignClient {


    @GetMapping("/internal/api-v1/courses/{courseId}")
    InternalApiResponse<CourseMetaResponse> getCourseById(@PathVariable("courseId") String courseId);


    @PostMapping("/internal/api-v1/courses/filter")
   ResponseEntity<List<CourseMetaResponse>> getSearchCourse(@RequestBody CourseFilterInternalRequest request);

}
