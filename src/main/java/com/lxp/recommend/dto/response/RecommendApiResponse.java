package com.lxp.recommend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendApiResponse(
        String userId,
        List<RecommendationItem> recommendations,
        String batchId,
        LocalDateTime generatedAt
) {
    public record RecommendationItem(
            String courseId,
            int rank
    ) {
    }

    public static RecommendApiResponse of(String userId, List<String> courseIds, String batchId, LocalDateTime generatedAt) {
        List<RecommendationItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < courseIds.size(); i++) {
            items.add(new RecommendationItem(courseIds.get(i), i + 1));
        }
        return new RecommendApiResponse(userId, items, batchId, generatedAt);
    }
}
