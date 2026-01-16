package com.lxp.recommend.application.dto.response;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import java.util.List;

public record RecommendationResponse(
        String memberId,
        List<RecommendedCourseDto> recommendations,
        String calculatedAt
) {}