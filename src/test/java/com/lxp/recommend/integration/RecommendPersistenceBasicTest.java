package com.lxp.recommend.integration;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.LearningHistoryData;
import com.lxp.recommend.application.port.required.CourseMetaQueryPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.application.service.RecommendCommandService;
import com.lxp.recommend.infrastructure.persistence.entity.MemberRecommendationJpaEntity;
import com.lxp.recommend.infrastructure.persistence.repository.JpaMemberRecommendationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 추천 데이터 기본 저장/조회 테스트
 */
@SpringBootTest
@ActiveProfiles("persistence")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecommendPersistenceBasicTest {

    @Autowired
    private RecommendCommandService commandService;

    @Autowired
    private JpaMemberRecommendationRepository repository;

    @MockitoBean
    private LearnerProfileQueryPort learnerProfilePort;

    @MockitoBean
    private CourseMetaQueryPort courseMetaPort;

    @MockitoBean
    private LearningHistoryQueryPort learningHistoryPort;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== [테스트 준비] DB 초기화 ===");
        repository.deleteAll();
        System.out.println("기존 데이터 삭제 완료\n");
    }

    @Test
    @Order(1)
    @DisplayName("[기본-1] 단일 사용자 추천 저장 및 조회")
    void saveSingleUserRecommendation() {
        // Given
        String learnerId = "user-single-001";

        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.of(new LearnerProfileData(
                        learnerId,
                        "MIDDLE",
                        Set.of("Java", "Spring", "JPA")
                )));

        when(courseMetaPort.findByDifficulties(anySet(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("course-java-001", Set.of("Java", "Spring"), "MIDDLE", true),
                        new CourseMetaData("course-jpa-002", Set.of("JPA", "Database"), "MIDDLE", true),
                        new CourseMetaData("course-python-003", Set.of("Python"), "MIDDLE", true),
                        new CourseMetaData("course-react-004", Set.of("React", "Frontend"), "MIDDLE", true)
                ));

        when(learningHistoryPort.findByLearnerId(learnerId))
                .thenReturn(List.of());

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [기본-1] 단일 사용자 추천 저장         ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        System.out.println("📌 테스트 사용자: " + learnerId);
        System.out.println("📌 학습자 레벨: MIDDLE");
        System.out.println("📌 관심 태그: Java, Spring, JPA\n");

        // When
        commandService.refreshRecommendation(learnerId);

        // Then
        Optional<MemberRecommendationJpaEntity> savedRec = repository.findByMemberId(learnerId);

        assertThat(savedRec).isPresent();
        MemberRecommendationJpaEntity recommendation = savedRec.get();

        System.out.println("✅ MySQL에 저장 완료!");
        System.out.printf("📊 저장된 추천 결과: 총 %d개%n%n", recommendation.getItems().size());

        recommendation.getItems().forEach(item -> {
            System.out.printf("  [%d위] 강좌: %-20s | 점수: %.2f%n",
                    item.getRank(),
                    item.getCourseId(),
                    item.getScore()
            );
        });

        System.out.println("\n════════════════════════════════════════════\n");

        // 검증
        assertThat(recommendation.getItems()).hasSizeGreaterThan(0);
        assertThat(recommendation.getMemberId()).isEqualTo(learnerId);
        assertThat(recommendation.getCalculatedAt()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("[기본-2] 추천 결과가 점수 내림차순으로 정렬되는지 확인")
    void verifyScoreOrdering() {
        // Given
        String learnerId = "user-order-test";

        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.of(new LearnerProfileData(
                        learnerId, "MIDDLE", Set.of("Java", "Spring")
                )));

        when(courseMetaPort.findByDifficulties(anySet(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("course-001", Set.of("Java", "Spring"), "MIDDLE", true),
                        new CourseMetaData("course-002", Set.of("Java"), "MIDDLE", true),
                        new CourseMetaData("course-003", Set.of("Python"), "MIDDLE", true)
                ));

        when(learningHistoryPort.findByLearnerId(learnerId))
                .thenReturn(List.of());

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [기본-2] 점수 정렬 확인                ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // When
        commandService.refreshRecommendation(learnerId);

        // Then
        MemberRecommendationJpaEntity recommendation = repository.findByMemberId(learnerId).get();

        System.out.println("✅ 점수 정렬 검증:");
        for (int i = 0; i < recommendation.getItems().size(); i++) {
            var item = recommendation.getItems().get(i);
            System.out.printf("  [%d위] 점수: %.2f%n", i + 1, item.getScore());
        }

        // 점수 내림차순 검증
        for (int i = 0; i < recommendation.getItems().size() - 1; i++) {
            assertThat(recommendation.getItems().get(i).getScore())
                    .isGreaterThanOrEqualTo(recommendation.getItems().get(i + 1).getScore());
        }

        System.out.println("\n✅ 점수 정렬 정상 확인\n");
    }

    @Test
    @Order(3)
    @DisplayName("[기본-3] 저장된 데이터를 조회할 수 있는지 확인")
    void retrieveSavedRecommendation() {
        // Given
        String learnerId = "user-retrieve-test";
        mockValidUserData(learnerId);
        commandService.refreshRecommendation(learnerId);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [기본-3] 저장 데이터 조회              ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // When
        Optional<MemberRecommendationJpaEntity> retrieved = repository.findByMemberId(learnerId);

        // Then
        assertThat(retrieved).isPresent();
        System.out.println("✅ 저장된 데이터 조회 성공");
        System.out.printf("   사용자: %s%n", retrieved.get().getMemberId());
        System.out.printf("   추천 수: %d개%n%n", retrieved.get().getItems().size());
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║         [기본 테스트 완료]                 ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("✅ 기본 CRUD 테스트 완료\n");
    }

    // Helper
    private void mockValidUserData(String learnerId) {
        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.of(new LearnerProfileData(
                        learnerId, "MIDDLE", Set.of("Java")
                )));

        when(courseMetaPort.findByDifficulties(anySet(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("course-001", Set.of("Java"), "MIDDLE", true)
                ));

        when(learningHistoryPort.findByLearnerId(learnerId))
                .thenReturn(List.of());
    }
}
