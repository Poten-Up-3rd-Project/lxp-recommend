package com.lxp.recommend.adapter;

import com.lxp.recommend.adapter.in.web.RecommendController;
import com.lxp.recommend.application.port.in.RecommendQueryUseCase;
import com.lxp.recommend.dto.response.RecommendApiResponse;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import com.lxp.recommend.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecommendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendQueryUseCase recommendQueryUseCase;

    @InjectMocks
    private RecommendController recommendController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recommendController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("추천 목록을 조회할 수 있다")
    void getRecommendations_success() throws Exception {
        RecommendApiResponse response = RecommendApiResponse.of(
                "user-1",
                List.of("course-1", "course-2", "course-3"),
                "batch_20260207_0600",
                LocalDateTime.of(2026, 2, 7, 6, 15, 30)
        );

        given(recommendQueryUseCase.getRecommendations("user-1", null))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/recommendations")
                        .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(3))
                .andExpect(jsonPath("$.recommendations[0].courseId").value("course-1"))
                .andExpect(jsonPath("$.recommendations[0].rank").value(1))
                .andExpect(jsonPath("$.batchId").value("batch_20260207_0600"));
    }

    @Test
    @DisplayName("limit 파라미터를 전달할 수 있다")
    void getRecommendations_withLimit() throws Exception {
        RecommendApiResponse response = RecommendApiResponse.of(
                "user-1",
                List.of("course-1", "course-2"),
                "batch_20260207_0600",
                LocalDateTime.now()
        );

        given(recommendQueryUseCase.getRecommendations("user-1", 2))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/recommendations")
                        .param("userId", "user-1")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations.length()").value(2));
    }

    @Test
    @DisplayName("추천 결과가 없으면 404를 반환한다")
    void getRecommendations_notFound() throws Exception {
        given(recommendQueryUseCase.getRecommendations("user-1", null))
                .willThrow(new BusinessException(ErrorCode.RECOMMENDATION_NOT_FOUND, "No recommendations found for user: user-1"));

        mockMvc.perform(get("/api/v1/recommendations")
                        .param("userId", "user-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("R001"));
    }

    @Test
    @DisplayName("userId가 없으면 400을 반환한다")
    void getRecommendations_missingUserId() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations"))
                .andExpect(status().isBadRequest());
    }
}
