package com.lxp.recommend.presentation;

import com.lxp.recommend.application.RecommendationApplicationService;
import com.lxp.recommend.application.dto.RecommendedCourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationService;

    /**
     * 내 맞춤 추천 강좌 조회 (Top 4)
     * @param memberIdHeader (임시) 게이트웨이나 필터에서 검증된 사용자 ID 헤더
     */
    @GetMapping("/me")
    public ResponseEntity<List<RecommendedCourseDto>> getMyRecommendations(
            @RequestHeader(value = "X-MEMBER-ID", required = false) String memberIdHeader
    ) {
        // 1. 사용자 ID 추출 (인증 로직 미정이므로 헤더 파싱 or 임시값 사용)
        UUID memberId;
        try {
            if (memberIdHeader != null && !memberIdHeader.isBlank()) {
                memberId = UUID.fromString(memberIdHeader);
            } else {
                // 개발용 임시 ID (나중에 인증 로직 확정 시 제거)
                // throw new IllegalArgumentException("로그인이 필요합니다.");
                memberId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 잘못된 UUID 포맷 등
        }

        // 2. 서비스 호출
        List<RecommendedCourseDto> result = recommendationService.getTopRecommendations(memberId);

        // 3. 응답
        return ResponseEntity.ok(result);
    }
}