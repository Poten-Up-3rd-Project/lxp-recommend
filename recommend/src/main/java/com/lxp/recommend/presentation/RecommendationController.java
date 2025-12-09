package com.lxp.recommend.presentation;

import com.lxp.recommend.application.RecommendationApplicationService;
import com.lxp.recommend.application.dto.RecommendedCourseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationService;

    /**
     * 내 맞춤 추천 강좌 조회 (Top 10)
     * @param memberIdHeader 인증된 사용자 ID (String, UUID format)
     */
    @GetMapping("/me")
    public ResponseEntity<List<RecommendedCourseDto>> getMyRecommendations(
            @RequestHeader(value = "X-MEMBER-ID", required = false) String memberIdHeader
    ) {
        String memberId = memberIdHeader;

        // 1. 사용자 ID 검증 및 임시 처리
        if (memberId == null || memberId.isBlank()) {
            // 개발 환경 편의를 위한 임시 Fallback (운영 환경에서는 에러 처리 필요)
            log.warn("X-MEMBER-ID 헤더 없음. 임시 테스트 ID 사용.");
            memberId = "test-member-uuid-001";
            // 또는 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("추천 요청 수신: memberId={}", memberId);

        // 2. 서비스 호출 (String ID 그대로 전달)
        // Service 메서드 시그니처가 (String)으로 변경되어 있어야 함
        List<RecommendedCourseDto> result = recommendationService.getTopRecommendations(memberId);

        // 3. 응답 (204 No Content 처리 선택 사항)
        if (result.isEmpty()) {
            // 빈 리스트([])를 줄지, 204를 줄지는 프론트와 합의. 보통 빈 리스트가 처리가 편함.
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(result);
    }
}
