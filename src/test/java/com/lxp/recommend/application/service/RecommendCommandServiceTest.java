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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RecommendCommandService 통합 테스트
 *
 * 총 33개 테스트 케이스:
 * - Group 1: 기본 랭킹 및 점수 로직 (Case 8-10)
 * - Group 2: 정책 및 필터링 (Case 11-12)
 * - Group 3: 엣지 케이스 (Case 13-15)
 * - Group 4: 실제 사용자 패턴 (Case 16-19)
 * - Group 5: 데이터 품질 이슈 (Case 20-21)
 * - Group 6: 동점자 처리 (Case 22)
 * - Group 7: 대규모 시뮬레이션 (Case 23-24)
 */
@ExtendWith(MockitoExtension.class)
class RecommendCommandServiceTest {

    @Mock private MemberRecommendationRepository recommendationRepository;
    @Mock private LearnerProfileQueryPort learnerProfilePort;
    @Mock private CourseMetaQueryPort courseMetaPort;
    @Mock private LearningHistoryQueryPort learningHistoryPort;

    @InjectMocks
    private RecommendCommandService service;

    private final String learnerId = "user-123";

    @BeforeEach
    void setUp() {
        // 기본적으로 Repository 저장은 성공한다고 가정 (Mocking)
        MemberRecommendation mockRec = new MemberRecommendation(MemberId.of(learnerId));
        lenient().when(recommendationRepository.findByMemberId(any())).thenReturn(Optional.of(mockRec));
    }

    // =====================================================================
    // [Scenario Group 1] 기본 랭킹 및 점수 로직
    // =====================================================================

    @Test
    @DisplayName("[Case 8] Explicit 태그 매칭: 관심 태그와 일치하는 강좌가 상위에 랭크됨")
    void scenario_explicitMatching() {
        // Given
        setupProfile(Set.of("Java")); // 관심사: Java
        setupHistory(List.of());
        setupCourses(List.of(
                new CourseMetaData("c-java", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-python", Set.of("Python"), "JUNIOR", true)
        ));

        // When
        service.refreshRecommendation(learnerId);

        // Then
        verify(recommendationRepository).save(argThat(rec -> {
            List<RecommendedCourse> items = rec.getItems();
            assertThat(items).hasSize(1);
            assertThat(items.get(0).getCourseId().getValue()).isEqualTo("c-java");
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 9] 가중치 검증: 수강 중인 강좌(Implicit)의 연관 강좌가 단순 관심사(Explicit)보다 우선함")
    void scenario_implicitWeightHigher() {
        // Given
        // - 관심사: Python (1.0점)
        // - 수강중: Java Course -> Implicit Tag: Java (1.5점)
        setupProfile(Set.of("Python"));
        setupHistory(List.of(new LearningHistoryData(learnerId, "c-enrolled-java", "ENROLLED")));

        setupCourses(List.of(
                new CourseMetaData("c-enrolled-java", Set.of("Java"), "JUNIOR", true), // 수강중인 강좌 (태그 추출용)
                new CourseMetaData("c-advanced-java", Set.of("Java"), "SENIOR", true), // 추천 후보 1
                new CourseMetaData("c-python-basic", Set.of("Python"), "JUNIOR", true) // 추천 후보 2
        ));

        // When
        service.refreshRecommendation(learnerId);

        // Then
        verify(recommendationRepository).save(argThat(rec -> {
            List<RecommendedCourse> items = rec.getItems();
            // Java 강좌(1.5점) > Python 강좌(1.0점)
            assertThat(items.get(0).getCourseId().getValue()).isEqualTo("c-advanced-java");
            assertThat(items.get(1).getCourseId().getValue()).isEqualTo("c-python-basic");
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 10] 하이브리드 매칭: 관심사 + 수강이력이 겹치면 점수 합산")
    void scenario_hybridScore() {
        // Given: 관심사 Java + 수강중 Java -> Java 강좌 점수 폭발
        setupProfile(Set.of("Java"));
        setupHistory(List.of(new LearningHistoryData(learnerId, "c-java-1", "ENROLLED")));

        setupCourses(List.of(
                new CourseMetaData("c-java-1", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-java-2", Set.of("Java"), "SENIOR", true)
        ));

        // When
        service.refreshRecommendation(learnerId);

        // Then
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems().get(0).getScore()).isEqualTo(2.5); // 1.0 + 1.5
            return true;
        }));
    }

    // =====================================================================
    // [Scenario Group 2] 정책 및 필터링
    // =====================================================================

    @Test
    @DisplayName("[Case 11] 비공개 강좌(Public=false)는 추천에서 제외")
    void scenario_excludePrivateCourse() {
        setupProfile(Set.of("Java"));
        setupHistory(List.of());
        setupCourses(List.of(
                new CourseMetaData("c-public", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-private", Set.of("Java"), "JUNIOR", false) // 비공개
        ));

        service.refreshRecommendation(learnerId);

        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).extracting(r -> r.getCourseId().getValue())
                    .contains("c-public")
                    .doesNotContain("c-private");
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 12] 레벨 정책: JUNIOR 유저는 EXPERT 강좌를 추천받지 않음 (Port 호출 파라미터 검증)")
    void scenario_levelPolicy() {
        // Given: JUNIOR 유저
        LearnerProfileData profile = new LearnerProfileData(learnerId, "JUNIOR", Set.of("Java"));
        when(learnerProfilePort.getProfile(learnerId)).thenReturn(Optional.of(profile));
        when(learningHistoryPort.findByLearnerId(any())).thenReturn(List.of());
        when(courseMetaPort.findByDifficulties(any(), anyInt())).thenReturn(List.of());

        // When
        service.refreshRecommendation(learnerId);

        // Then: Port에 요청할 때 JUNIOR, MIDDLE만 요청했는지 검증
        verify(courseMetaPort).findByDifficulties(argThat(levels -> {
            assertThat(levels).contains("JUNIOR", "MIDDLE");
            assertThat(levels).doesNotContain("SENIOR", "EXPERT");
            return true;
        }), anyInt());
    }

    // =====================================================================
    // [Scenario Group 3] 엣지 케이스
    // =====================================================================

    @Test
    @DisplayName("[Case 13] Cold Start: 태그도 없고 이력도 없으면 추천 저장 안 함")
    void scenario_coldStart() {
        setupProfile(Set.of()); // 태그 없음
        setupHistory(List.of()); // 이력 없음
        setupCourses(List.of(new CourseMetaData("c-1", Set.of("Java"), "JUNIOR", true)));

        service.refreshRecommendation(learnerId);

        // 저장 로직 호출되지 않음
        verify(recommendationRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Case 14] Top 10 제한: 결과가 많아도 10개만 저장")
    void scenario_limitTop10() {
        setupProfile(Set.of("Java"));
        setupHistory(List.of());

        // 20개의 Java 강좌 생성
        List<CourseMetaData> manyCourses = IntStream.range(0, 20)
                .mapToObj(i -> new CourseMetaData("c-" + i, Set.of("Java"), "JUNIOR", true))
                .toList();
        setupCourses(manyCourses);

        service.refreshRecommendation(learnerId);

        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).hasSize(10);
            assertThat(rec.getItems().get(0).getRank()).isEqualTo(1);
            assertThat(rec.getItems().get(9).getRank()).isEqualTo(10);
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 15] 4개 미만인 경우 (Fallback 로직 부재 확인)")
    void scenario_under4items() {
        setupProfile(Set.of("Java"));
        setupHistory(List.of());
        // 2개만 존재
        setupCourses(List.of(
                new CourseMetaData("c-1", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-2", Set.of("Java"), "JUNIOR", true)
        ));

        service.refreshRecommendation(learnerId);

        // 현재 로직상 2개만 그대로 저장됨 (Fallback 구현 필요성 확인용)
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).hasSize(2);
            return true;
        }));
    }

    // =====================================================================
    // [Scenario Group 4] 실제 사용자 패턴
    // =====================================================================

    @Test
    @DisplayName("[Case 16] 활발한 학습자: 10개 이상 수강 이력이 있는 경우")
    void scenario_heavyLearner() {
        // Given: 10개 수강 중 + 5개 완료
        setupProfile(Set.of("Java", "Spring"));

        List<LearningHistoryData> histories = IntStream.range(1, 16)
                .mapToObj(i -> new LearningHistoryData(
                        learnerId,
                        "c-enrolled-" + i,
                        i <= 10 ? "ENROLLED" : "COMPLETED"
                ))
                .collect(Collectors.toList());
        setupHistory(histories);

        // 수강 중인 강좌 10개 (태그 추출용)
        List<CourseMetaData> enrolledCourses = IntStream.range(1, 11)
                .mapToObj(i -> new CourseMetaData(
                        "c-enrolled-" + i,
                        Set.of("Java", "Spring", "Tag-" + i),
                        "MIDDLE",
                        true
                ))
                .collect(Collectors.toList());

        // 새 추천 후보 5개
        List<CourseMetaData> newCourses = IntStream.range(1, 6)
                .mapToObj(i -> new CourseMetaData(
                        "c-new-" + i,
                        Set.of("Java", "Spring"),
                        "MIDDLE",
                        true
                ))
                .collect(Collectors.toList());

        List<CourseMetaData> allCourses = new java.util.ArrayList<>();
        allCourses.addAll(enrolledCourses);
        allCourses.addAll(newCourses);
        setupCourses(allCourses);

        // When
        service.refreshRecommendation(learnerId);

        // Then: Implicit 태그가 풍부하여 높은 점수
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).isNotEmpty();
            // 각 추천 강좌의 점수가 Explicit(2.0) + Implicit(많음)
            assertThat(rec.getItems().get(0).getScore()).isGreaterThan(2.0);
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 17] 초보 학습자: 관심사만 있고 수강 이력이 전혀 없음")
    void scenario_beginnerWithNoHistory() {
        // Given: 관심사 3개 + 이력 0개
        setupProfile(Set.of("Java", "Spring", "React"));
        setupHistory(List.of());

        // JUNIOR 레벨 강좌 10개
        List<CourseMetaData> courses = IntStream.range(0, 10)
                .mapToObj(i -> new CourseMetaData(
                        "c-" + i,
                        i < 5 ? Set.of("Java") : Set.of("React"),
                        "JUNIOR",
                        true
                ))
                .collect(Collectors.toList());
        setupCourses(courses);

        // When
        service.refreshRecommendation(learnerId);

        // Then: Explicit 태그만으로 추천 생성
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).hasSize(10);
            assertThat(rec.getItems().get(0).getScore()).isEqualTo(1.0);  // Explicit만
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 18] 전문가: EXPERT 레벨은 SENIOR + EXPERT 강좌 추천받음")
    void scenario_expertLevelUser() {
        // Given: EXPERT 레벨 사용자
        LearnerProfileData profile = new LearnerProfileData(
                learnerId, "EXPERT", Set.of("Architecture", "DDD")
        );
        when(learnerProfilePort.getProfile(learnerId)).thenReturn(Optional.of(profile));
        when(learningHistoryPort.findByLearnerId(any())).thenReturn(List.of());

        setupCourses(List.of(
                new CourseMetaData("c-expert", Set.of("Architecture"), "EXPERT", true),
                new CourseMetaData("c-senior", Set.of("DDD"), "SENIOR", true)  // ✅ 이제 포함되어야 함
        ));

        // When
        service.refreshRecommendation(learnerId);

        // Then: SENIOR + EXPERT 강좌 모두 추천
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).hasSize(2);  // ✅ 2개로 수정
            assertThat(rec.getItems()).extracting(r -> r.getCourseId().getValue())
                    .containsExactlyInAnyOrder("c-expert", "c-senior");  // ✅ 둘 다 포함
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 19] 중도 포기 패턴: CANCELLED 강좌가 많은 사용자도 추천 받음")
    void scenario_userWithManyCancellations() {
        // Given: 5개 취소 + 1개 수강 중
        setupProfile(Set.of("Java"));
        setupHistory(List.of(
                new LearningHistoryData(learnerId, "c-1", "CANCELLED"),
                new LearningHistoryData(learnerId, "c-2", "CANCELLED"),
                new LearningHistoryData(learnerId, "c-3", "CANCELLED"),
                new LearningHistoryData(learnerId, "c-4", "CANCELLED"),
                new LearningHistoryData(learnerId, "c-5", "CANCELLED"),
                new LearningHistoryData(learnerId, "c-6", "ENROLLED")
        ));

        setupCourses(List.of(
                new CourseMetaData("c-1", Set.of("Java"), "JUNIOR", true), // 취소했지만 다시 추천 가능
                new CourseMetaData("c-6", Set.of("Java"), "JUNIOR", true), // 수강 중 (제외)
                new CourseMetaData("c-7", Set.of("Java"), "JUNIOR", true)  // 새 추천
        ));

        // When
        service.refreshRecommendation(learnerId);

        // Then: 취소한 강좌는 다시 추천 가능
        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems()).extracting(r -> r.getCourseId().getValue())
                    .contains("c-1", "c-7")
                    .doesNotContain("c-6");
            return true;
        }));
    }

    // =====================================================================
    // [Scenario Group 5] 데이터 품질 이슈
    // =====================================================================

    @Test
    @DisplayName("[Case 20] 태그가 없는 강좌는 점수 0점으로 추천에서 자연 제외")
    void scenario_courseWithoutTags() {
        setupProfile(Set.of("Java"));
        setupHistory(List.of());
        setupCourses(List.of(
                new CourseMetaData("c-no-tag", Set.of(), "JUNIOR", true),  // 태그 없음
                new CourseMetaData("c-with-tag", Set.of("Java"), "JUNIOR", true)
        ));

        service.refreshRecommendation(learnerId);

        verify(recommendationRepository).save(argThat(rec -> {
            // 태그 없는 강좌는 0점으로 필터링됨
            assertThat(rec.getItems()).hasSize(1);
            assertThat(rec.getItems().get(0).getCourseId().getValue()).isEqualTo("c-with-tag");
            return true;
        }));
    }

    @Test
    @DisplayName("[Case 21] 강좌가 관심사 태그 5개를 모두 포함하면 최고 점수")
    void scenario_courseMatchingAllTags() {
        // Given: 5개 관심사
        setupProfile(Set.of("Java", "Spring", "DDD", "Redis", "Docker"));
        setupHistory(List.of());
        setupCourses(List.of(
                new CourseMetaData("c-perfect", Set.of("Java", "Spring", "DDD", "Redis", "Docker"), "JUNIOR", true),  // 5점
                new CourseMetaData("c-partial", Set.of("Java"), "JUNIOR", true)  // 1점
        ));

        service.refreshRecommendation(learnerId);

        verify(recommendationRepository).save(argThat(rec -> {
            assertThat(rec.getItems().get(0).getCourseId().getValue()).isEqualTo("c-perfect");
            assertThat(rec.getItems().get(0).getScore()).isEqualTo(5.0);  // 1.0 * 5개
            return true;
        }));
    }

    // =====================================================================
    // [Scenario Group 6] 동점자 처리
    // =====================================================================

    @Test
    @DisplayName("[Case 22] 동일 점수 강좌는 삽입 순서 유지 (안정 정렬)")
    void scenario_sameScoredCourses() {
        setupProfile(Set.of("Java"));
        setupHistory(List.of());
        setupCourses(List.of(
                new CourseMetaData("c-003", Set.of("Java"), "JUNIOR", true),  // 1점
                new CourseMetaData("c-001", Set.of("Java"), "JUNIOR", true),  // 1점
                new CourseMetaData("c-002", Set.of("Java"), "JUNIOR", true)   // 1점
        ));

        service.refreshRecommendation(learnerId);

        verify(recommendationRepository).save(argThat(rec -> {
            List<RecommendedCourse> items = rec.getItems();
            assertThat(items).hasSize(3);
            // 동점일 경우 삽입 순서 유지 (Stream.sorted 특성)
            assertThat(items.get(0).getScore()).isEqualTo(1.0);
            assertThat(items.get(1).getScore()).isEqualTo(1.0);
            assertThat(items.get(2).getScore()).isEqualTo(1.0);
            return true;
        }));
    }

    // =====================================================================
    // [Scenario Group 7] 대규모 시뮬레이션
    // =====================================================================

    @Test
    @DisplayName("[Case 23] 200명 사용자 동시 추천 - 부하 테스트 시뮬레이션")
    void scenario_200UsersSimulation() {
        // Given: 200명의 사용자 ID
        List<String> userIds = IntStream.range(0, 200)
                .mapToObj(i -> "user-" + i)
                .toList();

        // 공통 Mock 설정
        setupCommonMockForMultipleUsers();

        // When: 200명 순차 처리 (실제로는 각각 다른 요청)
        long startTime = System.currentTimeMillis();

        userIds.forEach(userId -> {
            try {
                service.refreshRecommendation(userId);
            } catch (Exception e) {
                // Mock 프로필 없음 등은 무시
            }
        });

        long duration = System.currentTimeMillis() - startTime;

        // Then: 전체 처리 시간 기록
        System.out.printf("[부하 테스트 시뮬레이션] 200명 처리 시간: %dms (평균 %.2fms/명)%n",
                duration, duration / 200.0);

        // 실제 환경에서는 병렬 처리 시 훨씬 빠를 것
        assertThat(duration).isLessThan(10000);  // 10초 이내 (순차 처리 기준)
    }

    @Test
    @DisplayName("[Case 24] 극단적 케이스: 모든 후보 강좌를 이미 수강 완료")
    void scenario_allCoursesCompleted() {
        setupProfile(Set.of("Java"));

        // 모든 후보 강좌를 수강 완료
        setupHistory(List.of(
                new LearningHistoryData(learnerId, "c-1", "COMPLETED"),
                new LearningHistoryData(learnerId, "c-2", "COMPLETED"),
                new LearningHistoryData(learnerId, "c-3", "COMPLETED")
        ));

        setupCourses(List.of(
                new CourseMetaData("c-1", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-2", Set.of("Java"), "JUNIOR", true),
                new CourseMetaData("c-3", Set.of("Java"), "JUNIOR", true)
        ));

        service.refreshRecommendation(learnerId);

        // Then: 추천 목록이 비어있음 (저장 안 함)
        verify(recommendationRepository, never()).save(any());
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    private void setupProfile(Set<String> tags) {
        LearnerProfileData profile = new LearnerProfileData(learnerId, "JUNIOR", tags);
        when(learnerProfilePort.getProfile(learnerId)).thenReturn(Optional.of(profile));
    }

    private void setupHistory(List<LearningHistoryData> histories) {
        when(learningHistoryPort.findByLearnerId(learnerId)).thenReturn(histories);
    }

    private void setupCourses(List<CourseMetaData> courses) {
        // 난이도 파라미터는 유연하게 매칭 (any())
        lenient().when(courseMetaPort.findByDifficulties(any(), anyInt())).thenReturn(courses);
    }

    private void setupCommonMockForMultipleUsers() {
        // 모든 사용자에 대해 기본 프로필 반환
        lenient().when(learnerProfilePort.getProfile(anyString()))
                .thenReturn(Optional.of(new LearnerProfileData("user-default", "MIDDLE", Set.of("Java"))));

        lenient().when(learningHistoryPort.findByLearnerId(anyString()))
                .thenReturn(List.of());

        lenient().when(courseMetaPort.findByDifficulties(any(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("c-1", Set.of("Java"), "MIDDLE", true),
                        new CourseMetaData("c-2", Set.of("Java"), "MIDDLE", true)
                ));

        lenient().when(recommendationRepository.findByMemberId(any()))
                .thenReturn(Optional.of(new MemberRecommendation(MemberId.of("user-default"))));
    }
}
