package com.lxp.recommend.infrastructure.web.internal.client;

import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.enrollment.dto.EnrollmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Enrollment BC Internal API 호출용 FeignClient
 */
@FeignClient(
        name = "enrollment-service",
        url = "${external.enrollment.base-url}"
)
public interface EnrollmentServiceFeignClient {

    @GetMapping("/internal/api-v1/enrollments/learner/{learnerId}")
    InternalApiResponse<List<EnrollmentResponse>> getLearnerEnrollments(
            @PathVariable("learnerId") String learnerId
    );
}
