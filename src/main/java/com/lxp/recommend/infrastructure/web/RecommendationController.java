package com.lxp.recommend.infrastructure.web;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.application.service.RecommendCommandService;
import com.lxp.recommend.application.service.RecommendQueryService;
import com.lxp.common.infrastructure.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api-v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendCommandService commandService;
    private final RecommendQueryService queryService;

    // ✅ POST 요청으로 명시적 호출
    @PostMapping("/refresh")
    public ApiResponse<Void> refreshRecommendation(
            @RequestHeader("X-MEMBER-ID") String memberId
    ) {
        commandService.refreshRecommendation(memberId);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<List<RecommendedCourseDto>> getMyRecommendations(
            @RequestHeader(value = "X-MEMBER-ID", required = false) String memberIdHeader
    ) {
        String memberId = memberIdHeader != null ? memberIdHeader : "test-member-uuid-001";

        List<RecommendedCourseDto> result = queryService.getTopRecommendations(memberId);
        return ApiResponse.success(result);
    }
}
