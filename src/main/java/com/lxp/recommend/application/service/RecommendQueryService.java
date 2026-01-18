package com.lxp.recommend.application.service; // 또는 com.lxp.recommend.application.service.query

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;
import com.lxp.recommend.infrastructure.web.dto.response.RecommendedCourseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 추천 조회 Query Service
 *
 * 책임:
 * - 추천 결과 조회 (Repository)
 * - Domain Model → Response DTO 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendQueryService {

    private static final int DEFAULT_TOP_N = 10;
    private final MemberRecommendationRepository recommendationRepository;
    private final CourseMetaQueryPort courseMetaQueryPort;

    /**
     * 기본 상위 10개 추천 조회
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseResponse> getTopRecommendations(String memberId) {
        return getTopRecommendations(memberId, DEFAULT_TOP_N);
    }
    /**
     * 추천 결과 조회 (개수 지정)
     */
    @Transactional(readOnly = true)
    public List<RecommendedCourseResponse> getTopRecommendations(String memberId, int topN) {
        log.info("[추천 조회] memberId={}, topN={}", memberId, topN);

        MemberId memberIdObj = MemberId.of(memberId);

        // 1. Repository에서 조회
        MemberRecommendation recommendation = recommendationRepository
                .findByMemberId(memberIdObj)
                .orElse(null);

        // 2. 추천이 없으면 빈 리스트 반환
        if (recommendation == null || recommendation.isEmpty()) {
            log.info("[추천 없음] memberId={}", memberId);
            return Collections.emptyList();
        }

        // 3. 추천 목록 추출
        List<RecommendedCourse> items = recommendation.getItems().stream()
                .limit(topN)
                .toList();

        // 4. Course ID 목록 추출
        List<String> courseIds = items.stream()
                .map(item -> item.getCourseId().getValue())
                .toList();

        // 5. Course 메타 정보 조회
        List<CourseMetaResponse> courseMetas = courseMetaQueryPort.findByCourses(courseIds);

        // 6. Course ID → CourseMetaResponse 매핑
        Map<String, CourseMetaResponse> courseMetaMap = courseMetas.stream()
                .collect(Collectors.toMap(CourseMetaResponse::courseId, Function.identity()));

        // 7. 결과 조합
        return items.stream()
                .map(item -> {
                    CourseMetaResponse courseMeta = courseMetaMap.get(item.getCourseId().getValue());
                    return new RecommendedCourseResponse(
                            courseMeta,
                            item.getScore(),
                            item.getRank()
                    );
                })
                .filter(response -> response.course() != null)
                .toList();
    }
    /**
     * Domain(RecommendedCourse) → DTO(RecommendedCourseDto) 변환
     * (기존 RecommendedCourseMapper 로직 흡수)
     */
    private RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(),
                course.getScore(),
                course.getRank()
        );
    }
}
