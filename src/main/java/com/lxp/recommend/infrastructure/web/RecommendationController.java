package com.lxp.recommend.infrastructure.web;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.service.RecommendCommandService;
import com.lxp.recommend.application.service.RecommendQueryService;
import com.lxp.recommend.infrastructure.web.dto.response.RecommendationListResponse;
import com.lxp.recommend.infrastructure.web.external.passport.model.PassportClaims;
import com.lxp.recommend.infrastructure.web.support.PassportResolver;
import com.lxp.common.infrastructure.exception.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 추천 서비스 External API
 * 클라이언트(프론트엔드)에서 호출하는 엔드포인트
 */
@Slf4j
@RestController
@RequestMapping("/api-v1/recommend")
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "passport",
        name = "enabled",
        havingValue = "true"
)
public class RecommendationController {

    private final RecommendCommandService commandService;
    private final RecommendQueryService queryService;
    private final PassportResolver passportResolver;

    /**
     * 내 추천 강좌 목록 조회
     * GET /api-v1/recommendations/me
     */
    @GetMapping("/me")
    public ResponseEntity<RecommendationListResponse> getMyRecommendations(
            HttpServletRequest request
    ) {
        PassportClaims passport = passportResolver.resolve(request);
        log.info("Fetching recommendations for userId: {}", passport.userId());

        List<RecommendedCourseDto> dtos = queryService.getTopRecommendations(passport.userId());
        RecommendationListResponse response = RecommendationListResponse.from(dtos);

        return ResponseEntity
                .ok()
            .body(response);
    }

    /**
     * 추천 목록 갱신 (명시적 호출)
     * POST /api-v1/recommendations/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshRecommendation(
            HttpServletRequest request
    ) {
        PassportClaims passport = passportResolver.resolve(request);  //
        log.info("Refreshing recommendations for userId: {}", passport.userId());

        commandService.refreshRecommendation(passport.userId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
