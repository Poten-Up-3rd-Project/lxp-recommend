package com.lxp.recommend.infrastructure.external.course;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Course BC API 어댑터
 * CourseMetaQueryPort를 구현하여 실제 Course BC API 호출
 */
@Slf4j
@Component
@Profile("!(test | persistence)")
@RequiredArgsConstructor
public class CourseApiAdapter implements CourseMetaQueryPort {

    private final WebClient courseWebClient;

    @Override
    public List<CourseMetaData> findByDifficulties(Set<String> difficulties, int limit) {
        String difficultiesParam = String.join(",", difficulties);

        log.debug("[Course API] Fetching courses: difficulties={}, limit={}",
                difficultiesParam, limit);

        try {
            InternalApiResponse<List<CourseMetaResponse>> response = courseWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/api-v1/courses/search")
                            .queryParam("difficulties", difficultiesParam)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("[Course API] 4xx error: status={}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Course API client error"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("[Course API] 5xx error: status={}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Course service error"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<InternalApiResponse<List<CourseMetaResponse>>>() {})
                    .block();

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Course API] Failed or empty response");
                return List.of();
            }

            // DTO 변환
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
            InternalApiResponse<CourseMetaResponse> response = courseWebClient
                    .get()
                    .uri("/internal/api-v1/courses/{courseId}", courseId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("[Course API] Course not found: {}", courseId);
                        return Mono.error(new RuntimeException("Course not found: " + courseId));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("[Course API] 5xx error: status={}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Course service error"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<InternalApiResponse<CourseMetaResponse>>() {})
                    .block();

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
