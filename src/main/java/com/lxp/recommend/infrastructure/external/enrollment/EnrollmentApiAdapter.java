package com.lxp.recommend.infrastructure.external.enrollment;

import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.enrollment.dto.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Enrollment BC API 어댑터
 * LearningHistoryQueryPort를 구현하여 실제 Enrollment BC API 호출
 */
@Slf4j
@Component
@Profile("!test")  // test 프로파일이 아닐 때만 활성화
@RequiredArgsConstructor
public class EnrollmentApiAdapter implements LearningHistoryQueryPort {

    private final WebClient enrollmentWebClient;

    @Override
    public List<LearningHistoryData> findByLearnerId(String learnerId) {
        log.debug("[Enrollment API] Fetching enrollments for learnerId={}", learnerId);

        try {
            InternalApiResponse<List<EnrollmentResponse>> response = enrollmentWebClient
                    .get()
                    .uri("/internal/api-v1/enrollments/learner/{learnerId}", learnerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("[Enrollment API] 4xx error for learnerId={}, status={}",
                                learnerId, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Enrollment not found: " + learnerId));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("[Enrollment API] 5xx error for learnerId={}, status={}",
                                learnerId, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Enrollment service error"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<InternalApiResponse<List<EnrollmentResponse>>>() {})
                    .block();

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Enrollment API] Failed or empty response for learnerId={}", learnerId);
                return List.of();
            }

            // DTO 변환
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
