package com.lxp.recommend.infrastructure.web.internal.client;

import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.enrollment.dto.EnrollmentResponse;
import com.lxp.recommend.infrastructure.web.internal.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Enrollment BC Internal API 호출용 FeignClient
 */
@FeignClient(
        name = "enrollment-service",
        url = "${external.enrollment.base-url}",
        configuration = FeignConfig.class
)
public interface EnrollmentServiceFeignClient {

    @GetMapping("/internal/api-v1/enrollments/myEnrollmentsForRecommendation")
    ResponseEntity<List<EnrollmentResponse>> getLearnerEnrollments();
}
