package com.lxp.recommend.domain;

import com.lxp.recommend.domain.result.entity.RecommendResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RecommendResultTest {

    @Test
    @DisplayName("추천 결과를 생성할 수 있다")
    void createResult() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3"))
                .batchId("batch_20260207_0600")
                .build();

        assertThat(result.getUserId()).isEqualTo("user-1");
        assertThat(result.getCourseIds()).containsExactly("course-1", "course-2", "course-3");
        assertThat(result.getBatchId()).isEqualTo("batch_20260207_0600");
    }

    @Test
    @DisplayName("추천 결과를 업데이트할 수 있다")
    void updateRecommendations() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(new ArrayList<>(List.of("course-1", "course-2")))
                .batchId("batch_20260207_0600")
                .build();

        result.updateRecommendations(
                List.of("course-3", "course-4", "course-5"),
                "batch_20260207_1200"
        );

        assertThat(result.getCourseIds()).containsExactly("course-3", "course-4", "course-5");
        assertThat(result.getBatchId()).isEqualTo("batch_20260207_1200");
    }
}
