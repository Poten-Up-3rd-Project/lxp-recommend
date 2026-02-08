package com.lxp.recommend.adapter.in.web;

import com.lxp.recommend.application.port.in.RecommendQueryUseCase;
import com.lxp.recommend.dto.response.RecommendApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendQueryUseCase recommendQueryUseCase;

    @GetMapping
    public ResponseEntity<RecommendApiResponse> getRecommendations(
            @RequestParam String userId,
            @RequestParam(required = false) Integer limit
    ) {
        RecommendApiResponse response = recommendQueryUseCase.getRecommendations(userId, limit);
        return ResponseEntity.ok(response);
    }
}
