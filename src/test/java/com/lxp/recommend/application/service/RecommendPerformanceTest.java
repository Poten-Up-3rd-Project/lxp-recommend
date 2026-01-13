package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.provided.persistence.MemberRecommendationRepository;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.ids.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 추천 로직 성능 테스트
 * 대량 Mock 데이터로 처리 시간 측정
 */
@ExtendWith(MockitoExtension.class)
class RecommendPerformanceTest {

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

    private String testLearnerId = "perf-test-user";
    private LearnerProfileData mockProfile;

    @BeforeEach
    void setUp() {
        // 학습자 프로필 (관심사 10개)
        Set<String> interestTags = IntStream.range(0, 10)
                .mapToObj(i -> "Tag-" + i)
                .collect(Collectors.toSet());

        mockProfile = new LearnerProfileData(testLearnerId, "MIDDLE", interestTags);
    }

    @Test
    @DisplayName("성능 테스트: 100개 강좌 처리")
    void performanceTest_100Courses() {
        testPerformance(100, 10);
    }

    @Test
    @DisplayName("성능 테스트: 500개 강좌 처리")
    void performanceTest_500Courses() {
        testPerformance(500, 50);
    }

    @Test
    @DisplayName("성능 테스트: 1000개 강좌 처리")
    void performanceTest_1000Courses() {
        testPerformance(1000, 100);
    }

    @Test
    @DisplayName("성능 테스트: 5000개 강좌 처리 (대규모)")
    void performanceTest_5000Courses() {
        testPerformance(5000, 500);
    }

    /**
     * 성능 측정 헬퍼 메서드
     *
     * @param courseCount 테스트할 강좌 개수
     * @param historyCount 학습 이력 개수
     */
    private void testPerformance(int courseCount, int historyCount) {
        // 1. Mock 데이터 생성
        List<CourseMetaData> mockCourses = generateMockCourses(courseCount);
        List<LearningHistoryData> mockHistory = generateMockHistory(historyCount);

        // 2. Mock 설정
        when(learnerProfilePort.getProfile(testLearnerId)).thenReturn(Optional.of(mockProfile));
        when(courseMetaPort.findByDifficulties(anySet(), anyInt())).thenReturn(mockCourses);
        when(learningHistoryPort.findByLearnerId(testLearnerId)).thenReturn(mockHistory);

        MemberRecommendation mockRecommendation = new MemberRecommendation(MemberId.of(testLearnerId));
        when(recommendationRepository.findByMemberId(any())).thenReturn(Optional.of(mockRecommendation));
        when(recommendationRepository.save(any())).thenReturn(mockRecommendation);

        // 3. 성능 측정
        long startTime = System.currentTimeMillis();

        service.refreshRecommendation(testLearnerId);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 4. 결과 출력 및 검증
        System.out.printf("[성능 측정] 강좌: %d개, 이력: %d개, 소요 시간: %dms%n",
                courseCount, historyCount, duration);

        // 성능 기준: 1000개 강좌는 1초 이내 처리
        if (courseCount <= 1000) {
            assertThat(duration).isLessThan(1000);  // 1초
        } else {
            assertThat(duration).isLessThan(3000);  // 3초
        }
    }

    /**
     * Mock 강좌 데이터 생성
     * - 태그 1~5개 랜덤 조합
     * - 난이도 랜덤
     */
    private List<CourseMetaData> generateMockCourses(int count) {
        Random random = new Random(42);  // 재현 가능한 시드
        String[] levels = {"JUNIOR", "MIDDLE", "SENIOR", "EXPERT"};
        String[] allTags = IntStream.range(0, 20).mapToObj(i -> "Tag-" + i).toArray(String[]::new);

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    // 랜덤 태그 1~5개 선택
                    int tagCount = random.nextInt(5) + 1;
                    Set<String> tags = random.ints(tagCount, 0, allTags.length)
                            .mapToObj(idx -> allTags[idx])
                            .collect(Collectors.toSet());

                    String difficulty = levels[random.nextInt(levels.length)];
                    return new CourseMetaData("course-" + i, tags, difficulty, true);
                })
                .toList();
    }

    /**
     * Mock 학습 이력 생성
     * - 수강 완료/진행 중 강좌
     */
    private List<LearningHistoryData> generateMockHistory(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new LearningHistoryData(
                        testLearnerId,
                        "course-" + i,
                        i % 3 == 0 ? "COMPLETED" : "ENROLLED"
                ))
                .toList();
    }
}
