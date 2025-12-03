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

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationService;

    /**
     * 내 맞춤 추천 강좌 조회 (Top 4)
     * @param memberIdHeader (임시) 게이트웨이나 필터에서 검증된 사용자 ID 헤더 (String -> Long 파싱 필요)
     */
    @GetMapping("/me")
    public ResponseEntity<List<RecommendedCourseDto>> getMyRecommendations(
            @RequestHeader(value = "X-MEMBER-ID", required = false) String memberIdHeader
    ) {
        // 1. 사용자 ID 추출
        Long memberId;
        try {
            if (memberIdHeader != null && !memberIdHeader.isBlank()) {
                // UUID.fromString() -> Long.parseLong() 변경
                memberId = Long.parseLong(memberIdHeader);
            } else {
                // 개발용 임시 ID (UUID 포맷이 아닌 Long 숫자형으로 변경)
                // 예: 1번 회원
                memberId = 1L;
            }
        } catch (NumberFormatException e) { // IllegalArgumentException -> NumberFormatException
            return ResponseEntity.badRequest().build(); // 숫자가 아닌 값이 헤더에 들어옴
        }

        // 2. 서비스 호출 (이제 Long 타입을 받음)
        List<RecommendedCourseDto> result = recommendationService.getTopRecommendations(memberId);

        // 3. 응답
        return ResponseEntity.ok(result);
    }
}
