package com.lxp.recommend.interfaces;

import com.lxp.recommend.application.service.RecommendFacadeService;  // ← 변경
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

    private final RecommendFacadeService recommendFacadeService;  // ← 변경

    @GetMapping("/me")
    public ResponseEntity<List<RecommendedCourseDto>> getMyRecommendations(
            @RequestHeader(value = "X-MEMBER-ID", required = false) String memberIdHeader
    ) {
        String memberId = memberIdHeader != null ? memberIdHeader : "test-member-uuid-001";
        log.info("추천 요청 수신: memberId={}", memberId);

        List<RecommendedCourseDto> result = recommendFacadeService.getTopRecommendations(memberId);  // ← 변경

        return ResponseEntity.ok(result);
    }
}
