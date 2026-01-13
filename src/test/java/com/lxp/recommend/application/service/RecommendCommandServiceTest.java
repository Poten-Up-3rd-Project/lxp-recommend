package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RecommendCommandService 통합 테스트
 * Mock 데이터로 추천 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class RecommendCommandServiceTest {

    @Mock
    private MemberRecommendationRepository recommendationRepository;

    @Mock
    private LearnerProfileQueryPort learnerProfilePort;

    @Mock
    private CourseMetaQueryPort courseMetaPort;

    @Mock
    private LearningHistoryQueryPort learningHistoryPort;

    @InjectMocks
    private RecommendCommandService service;

    private String testLearnerId;
    private LearnerProfileData mockProfile;
    private List<CourseMetaData> mockCourses;
    private List<LearningHistoryData> mockHistory;

    @BeforeEach
    void setUp() {
        testLearnerId = "user-001";

        // 1. Mock 학습자 프로필
        mockProfile = new LearnerProfileData(
                testLearnerId,
                "MIDDLE",
                Set.of("Spring", "DDD", "Redis")  // Explicit Tags
        );

        // 2. Mock 강좌 후보
        mockCourses = List.of(
                new CourseMetaData("course-001", Set.of("Spring", "Java", "DDD"), "MIDDLE", true),   // 점수: 2.0
                new CourseMetaData("course-002", Set.of("React", "TypeScript"), "JUNIOR", true),      // 점수: 0.0 (제외)
                new CourseMetaData("course-003", Set.of("Redis", "Docker"), "SENIOR", true),          // 점수: 1.0
                new CourseMetaData("course-004", Set.of("Spring", "Kafka"), "MIDDLE", true),          // 수강 완료 (제외)
                new CourseMetaData("course-005", Set.of("DDD", "Kubernetes"), "SENIOR", true)         // 점수: 1.0
        );

        // 3. Mock 학습 이력 (course-004 수강 완료)
        mockHistory = List.of(
                new LearningHistoryData(testLearnerId, "course-004", "COMPLETED")
        );
    }

    @Test
    @DisplayName("Mock 데이터로 추천 계산 - 정상 플로우")
    void refreshRecommendation_withMockData_success() {
        // Given
        when(learnerProfilePort.getProfile(testLearnerId)).thenReturn(Optional.of(mockProfile));
        when(courseMetaPort.findByDifficulties(anySet(), eq(50))).thenReturn(mockCourses);
        when(learningHistoryPort.findByLearnerId(testLearnerId)).thenReturn(mockHistory);

        MemberRecommendation mockRecommendation = new MemberRecommendation(MemberId.of(testLearnerId));
        when(recommendationRepository.findByMemberId(any())).thenReturn(Optional.of(mockRecommendation));
        when(recommendationRepository.save(any())).thenReturn(mockRecommendation);

        // When
        service.refreshRecommendation(testLearnerId);

        // Then
        verify(recommendationRepository, times(1)).save(argThat(recommendation -> {
            List<RecommendedCourse> items = recommendation.getItems();

            // 검증 1: 추천 결과가 있는가?
            assertThat(items).isNotEmpty();

            // 검증 2: course-004 (수강 완료)가 제외되었는가?
            assertThat(items).noneMatch(item -> item.getCourseId().getValue().equals("course-004"));

            // 검증 3: 점수가 높은 순서로 정렬되었는가?
            for (int i = 0; i < items.size() - 1; i++) {
                assertThat(items.get(i).getScore()).isGreaterThanOrEqualTo(items.get(i + 1).getScore());
            }

            // 검증 4: 1순위는 course-001인가? (Spring + DDD 매칭)
            assertThat(items.get(0).getCourseId().getValue()).isEqualTo("course-001");
            assertThat(items.get(0).getRank()).isEqualTo(1);

            return true;
        }));
    }

    @Test
    @DisplayName("학습자 프로필이 없으면 예외 발생")
    void refreshRecommendation_noProfile_throwsException() {
        // Given
        when(learnerProfilePort.getProfile(testLearnerId)).thenReturn(Optional.empty());

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.refreshRecommendation(testLearnerId)
        );

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    @DisplayName("추천 가능한 강좌가 없으면 저장하지 않음")
    void refreshRecommendation_noCandidates_doesNotSave() {
        // Given
        when(learnerProfilePort.getProfile(testLearnerId)).thenReturn(Optional.of(mockProfile));
        when(courseMetaPort.findByDifficulties(anySet(), eq(50))).thenReturn(List.of());  // 빈 목록
        when(learningHistoryPort.findByLearnerId(testLearnerId)).thenReturn(mockHistory);

        // When
        service.refreshRecommendation(testLearnerId);

        // Then
        verify(recommendationRepository, never()).save(any());
    }
}
