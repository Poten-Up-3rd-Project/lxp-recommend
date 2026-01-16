package com.lxp.recommend.infrastructure.external.enrollment;

import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.enrollment.dto.EnrollmentResponse;
import com.lxp.recommend.infrastructure.web.internal.client.EnrollmentServiceFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Enrollment BC API 어댑터 (Feign 기반)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentApiAdapter implements LearningHistoryQueryPort {

    private final EnrollmentServiceFeignClient feignClient;  // ✅ WebClient → FeignClient

    @Override
    public List<LearningHistoryData> findByLearnerId(String learnerId) {
        log.debug("[Enrollment API] Fetching enrollments for learnerId={}", learnerId);

        try {
            InternalApiResponse<List<EnrollmentResponse>> response =
                    feignClient.getLearnerEnrollments(learnerId);

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Enrollment API] Failed or empty response for learnerId={}", learnerId);
                return List.of();
            }

            List<LearningHistoryData> histories = response.data().stream()
                    .map(enrollment -> new LearningHistoryData(
                            learnerId,
                            enrollment.courseId(),
                            enrollment.status()
                    ))
                    .toList();

            log.debug("[Enrollment API] Successfully fetched {} enrollments for learnerId={}",
                    histories.size(), learnerId);

            return histories;

        } catch (Exception e) {
            log.error("[Enrollment API] Error fetching enrollments for learnerId={}", learnerId, e);
            return List.of();
        }
    }
}
