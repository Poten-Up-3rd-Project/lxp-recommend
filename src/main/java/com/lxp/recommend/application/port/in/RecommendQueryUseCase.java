package com.lxp.recommend.application.port.in;

import com.lxp.recommend.dto.response.RecommendApiResponse;

public interface RecommendQueryUseCase {

    RecommendApiResponse getRecommendations(String userId, Integer limit);
}
