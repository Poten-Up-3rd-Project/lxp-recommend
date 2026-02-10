package com.lxp.recommend.application;

import com.lxp.recommend.application.port.out.ResultRepository;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.application.service.RecommendQueryService;
import com.lxp.recommend.domain.result.entity.RecommendResult;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.dto.response.RecommendApiResponse;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendQueryUseCaseTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendQueryService recommendQueryService;

    @Test
    @DisplayName("사용자의 추천 결과를 조회할 수 있다")
    void getRecommendations_success() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3"))
                .batchId("batch_20260207_0600")
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", null);

        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.recommendations()).hasSize(3);
        assertThat(response.recommendations().get(0).courseId()).isEqualTo("course-1");
        assertThat(response.recommendations().get(0).rank()).isEqualTo(1);
        assertThat(response.batchId()).isEqualTo("batch_20260207_0600");
    }

    @Test
    @DisplayName("limit 파라미터로 결과 개수를 제한할 수 있다")
    void getRecommendations_withLimit() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3", "course-4", "course-5"))
                .batchId("batch_20260207_0600")
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", 3);

        assertThat(response.recommendations()).hasSize(3);
    }

    @Test
    @DisplayName("이미 수강 중인 강의는 추천에서 제외된다")
    void getRecommendations_excludesEnrolledCourses() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3", "course-4"))
                .batchId("batch_20260207_0600")
                .build();

        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(List.of("course-1", "course-3"))
                .createdCourseIds(List.of())
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.of(user));

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", 10);

        assertThat(response.recommendations()).hasSize(2);
        assertThat(response.recommendations().stream().map(RecommendApiResponse.RecommendationItem::courseId))
                .containsExactly("course-2", "course-4");
    }

    @Test
    @DisplayName("본인이 만든 강의는 추천에서 제외된다")
    void getRecommendations_excludesCreatedCourses() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3"))
                .batchId("batch_20260207_0600")
                .build();

        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(List.of())
                .createdCourseIds(List.of("course-2"))
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.of(user));

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", 10);

        assertThat(response.recommendations()).hasSize(2);
        assertThat(response.recommendations().stream().map(RecommendApiResponse.RecommendationItem::courseId))
                .containsExactly("course-1", "course-3");
    }

    @Test
    @DisplayName("추천 결과가 없으면 예외가 발생한다")
    void getRecommendations_notFound() {
        given(resultRepository.findByUserId("user-1")).willReturn(Optional.empty());

        assertThatThrownBy(() -> recommendQueryService.getRecommendations("user-1", null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RECOMMENDATION_NOT_FOUND);
    }

    @Test
    @DisplayName("limit이 0이하면 기본값 7이 적용된다")
    void getRecommendations_defaultLimit() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10"))
                .batchId("batch_20260207_0600")
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", 0);

        assertThat(response.recommendations()).hasSize(7);
    }

    @Test
    @DisplayName("limit이 20을 초과하면 20으로 제한된다")
    void getRecommendations_maxLimit() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10",
                        "c11", "c12", "c13", "c14", "c15", "c16", "c17", "c18", "c19", "c20",
                        "c21", "c22", "c23", "c24", "c25"))
                .batchId("batch_20260207_0600")
                .build();

        given(resultRepository.findByUserId("user-1")).willReturn(Optional.of(result));
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        RecommendApiResponse response = recommendQueryService.getRecommendations("user-1", 100);

        assertThat(response.recommendations()).hasSize(20);
    }
}
