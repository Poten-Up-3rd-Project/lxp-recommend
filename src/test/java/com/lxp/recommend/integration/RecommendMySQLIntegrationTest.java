package com.lxp.recommend.integration;

import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.dto.CourseMetaData;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;  // ✅ 변경

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * MySQL 기반 추천 결과 저장 검증 테스트
 *
 * 목적: 실제 MySQL DB에 추천 데이터가 저장되는지 확인
 * 확인 방법: DBeaver에서 lxp_recommend_test DB 조회
 */
@SpringBootTest
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecommendMySQLIntegrationTest {

    @Autowired
    private RecommendCommandService commandService;

    @Autowired
    private JpaMemberRecommendationRepository recommendRepository;  // ✅ 수정

    // Spring Boot 3.4+ 사용 (3.5.9)
    @MockitoBean  // ✅ 변경: @MockBean → @MockitoBean
    private LearnerProfileQueryPort learnerProfilePort;

    @MockitoBean
    private CourseMetaQueryPort courseMetaPort;

    @MockitoBean
    private LearningHistoryQueryPort learningHistoryPort;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== [테스트 준비] DB 초기화 ===");
        recommendRepository.deleteAll();
        System.out.println("기존 데이터 삭제 완료\n");
    }

    @Test
    @Order(1)
    @DisplayName("[MySQL-1] 단일 사용자 추천 저장 및 DBeaver 확인")
    void saveSingleUserRecommendation() {
        // Given: Mock 데이터 설정
        String learnerId = "mysql-test-user-001";

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

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         [MySQL 저장 테스트] 단일 사용자 추천 저장          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("📌 테스트 사용자: " + learnerId);
        System.out.println("📌 학습자 레벨: MIDDLE");
        System.out.println("📌 관심 태그: Java, Spring, JPA\n");

        // When: 추천 계산 및 저장
        commandService.refreshRecommendation(learnerId);

        // Then: DB에서 조회
        Optional<MemberRecommendationJpaEntity> savedRec =
                recommendRepository.findByMemberId(learnerId);

        assertThat(savedRec).isPresent();

        MemberRecommendationJpaEntity recommendation = savedRec.get();

        System.out.println("✅ MySQL에 저장 완료!\n");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.printf("📊 저장된 추천 결과 (총 %d개)%n", recommendation.getItems().size());
        System.out.println("═══════════════════════════════════════════════════════════\n");

        recommendation.getItems().forEach(item -> {
            System.out.printf("  [%d위] 강좌: %-20s | 점수: %.2f%n",
                    item.getRank(),
                    item.getCourseId(),
                    item.getScore()
            );
        });

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("🔍 DBeaver에서 확인하세요!");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("1. DBeaver 실행");
        System.out.println("2. lxp_recommend_test DB 선택");
        System.out.println("3. SQL 실행:");
        System.out.println("\n   -- 부모 테이블");
        System.out.println("   SELECT * FROM member_recommendations;");
        System.out.println("\n   -- 자식 테이블 (추천 아이템)");
        System.out.println("   SELECT * FROM recommended_course_items;");
        System.out.println("\n   -- 조인 쿼리");
        System.out.println("   SELECT ");
        System.out.println("       mr.member_id,");
        System.out.println("       rci.course_id,");
        System.out.println("       rci.score,");
        System.out.println("       rci.rank_val");
        System.out.println("   FROM member_recommendations mr");
        System.out.println("   JOIN recommended_course_items rci ON mr.id = rci.recommendation_id");
        System.out.println("   ORDER BY mr.member_id, rci.rank_val;");
        System.out.println("\n═══════════════════════════════════════════════════════════\n");

        // 검증
        assertThat(recommendation.getItems()).hasSizeGreaterThan(0);
        assertThat(recommendation.getMemberId()).isEqualTo(learnerId);
        assertThat(recommendation.getCalculatedAt()).isNotNull();

        // 순위 검증
        for (int i = 0; i < recommendation.getItems().size() - 1; i++) {
            assertThat(recommendation.getItems().get(i).getScore())
                    .isGreaterThanOrEqualTo(recommendation.getItems().get(i + 1).getScore());
        }

        // 5초 대기 (DBeaver 조회 시간)
        try {
            System.out.println("⏳ 5초 대기 중... (DBeaver로 확인하세요)\n");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Order(2)
    @DisplayName("[MySQL-2] 다중 사용자 추천 저장")
    void saveMultipleUsersRecommendation() {
        // Given: 3명의 테스트 사용자
        List<String> userIds = List.of(
                "mysql-user-junior",
                "mysql-user-middle",
                "mysql-user-senior"
        );

        List<String> levels = List.of("JUNIOR", "MIDDLE", "SENIOR");

        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            String level = levels.get(i);

            when(learnerProfilePort.getProfile(userId))
                    .thenReturn(Optional.of(new LearnerProfileData(
                            userId, level, Set.of("Java")
                    )));
        }

        when(courseMetaPort.findByDifficulties(anySet(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("course-001", Set.of("Java"), "JUNIOR", true),
                        new CourseMetaData("course-002", Set.of("Java"), "MIDDLE", true),
                        new CourseMetaData("course-003", Set.of("Java"), "SENIOR", true)
                ));

        when(learningHistoryPort.findByLearnerId(anyString()))
                .thenReturn(List.of());

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         [MySQL 저장 테스트] 다중 사용자 추천 저장          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // When: 3명의 사용자 추천 계산
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            String level = levels.get(i);

            System.out.printf("📌 사용자 %d: %s (레벨: %s)%n", i + 1, userId, level);
            commandService.refreshRecommendation(userId);
        }

        // Then: DB 조회
        List<MemberRecommendationJpaEntity> allRecs = recommendRepository.findAll();

        System.out.println("\n✅ 총 저장된 회원 추천: " + allRecs.size() + "명\n");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("📊 사용자별 추천 수");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        allRecs.forEach(rec -> {
            System.out.printf("  %-25s : %d개 추천%n",
                    rec.getMemberId(),
                    rec.getItems().size());
        });

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("🔍 DBeaver 확인 쿼리:");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("\n   SELECT ");
        System.out.println("       mr.member_id,");
        System.out.println("       COUNT(rci.id) as recommendation_count,");
        System.out.println("       AVG(rci.score) as avg_score,");
        System.out.println("       MAX(rci.score) as max_score");
        System.out.println("   FROM member_recommendations mr");
        System.out.println("   LEFT JOIN recommended_course_items rci ON mr.id = rci.recommendation_id");
        System.out.println("   GROUP BY mr.member_id;");
        System.out.println("\n═══════════════════════════════════════════════════════════\n");

        // 검증
        assertThat(allRecs).hasSize(3);
        userIds.forEach(userId -> {
            assertThat(allRecs)
                    .anyMatch(rec -> rec.getMemberId().equals(userId));
        });
    }

    @Test
    @Order(3)
    @DisplayName("[MySQL-3] 추천 덮어쓰기 확인")
    void verifyRecommendationOverwrite() {
        // Given
        String learnerId = "mysql-overwrite-test";

        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.of(new LearnerProfileData(
                        learnerId, "MIDDLE", Set.of("Java")
                )));

        when(courseMetaPort.findByDifficulties(anySet(), anyInt()))
                .thenReturn(List.of(
                        new CourseMetaData("course-001", Set.of("Java"), "MIDDLE", true),
                        new CourseMetaData("course-002", Set.of("Spring"), "MIDDLE", true)
                ));

        when(learningHistoryPort.findByLearnerId(learnerId))
                .thenReturn(List.of());

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         [MySQL 저장 테스트] 추천 덮어쓰기 확인             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // When: 첫 번째 추천
        System.out.println("📌 1차 추천 실행...");
        commandService.refreshRecommendation(learnerId);

        Optional<MemberRecommendationJpaEntity> firstRec =
                recommendRepository.findByMemberId(learnerId);

        assertThat(firstRec).isPresent();
        int firstCount = firstRec.get().getItems().size();
        Long firstRecId = firstRec.get().getId();

        System.out.println("✅ 1차 저장 완료: " + firstCount + "개 (ID: " + firstRecId + ")\n");

        // When: 두 번째 추천 (덮어쓰기)
        System.out.println("📌 2차 추천 실행 (덮어쓰기)...");
        commandService.refreshRecommendation(learnerId);

        Optional<MemberRecommendationJpaEntity> secondRec =
                recommendRepository.findByMemberId(learnerId);

        assertThat(secondRec).isPresent();
        int secondCount = secondRec.get().getItems().size();
        Long secondRecId = secondRec.get().getId();

        System.out.println("✅ 2차 저장 완료: " + secondCount + "개 (ID: " + secondRecId + ")\n");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("📊 덮어쓰기 결과");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.printf("  1차 저장: %d개 (ID: %d)%n", firstCount, firstRecId);
        System.out.printf("  2차 저장: %d개 (ID: %d)%n", secondCount, secondRecId);
        System.out.println("  결과: " + (firstRecId.equals(secondRecId) ? "업데이트" : "재생성"));
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // 검증: 여전히 1명만 존재
        List<MemberRecommendationJpaEntity> allRecs = recommendRepository.findAll();
        assertThat(allRecs).hasSize(1);
        assertThat(allRecs.get(0).getMemberId()).isEqualTo(learnerId);
    }

    @Test
    @Order(4)
    @DisplayName("[MySQL-4] 추천 불가능한 경우 확인")
    void verifyEmptyRecommendationNotSaved() {
        // Given: 프로필 없는 사용자
        String learnerId = "user-no-profile";

        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.empty());

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         [MySQL 저장 테스트] 빈 추천 처리 확인              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("📌 프로필 없는 사용자: " + learnerId);

        // When: 추천 시도
        commandService.refreshRecommendation(learnerId);

        // Then: DB에 저장되지 않음
        Optional<MemberRecommendationJpaEntity> savedRec =
                recommendRepository.findByMemberId(learnerId);

        System.out.println("✅ 프로필 없는 사용자는 저장 안 됨: " + savedRec.isEmpty() + "\n");

        assertThat(savedRec).isEmpty();
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  [테스트 완료]                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n✅ 모든 테스트 완료!");
        System.out.println("📂 DB: lxp_recommend_test");
        System.out.println("📊 테이블:");
        System.out.println("   - member_recommendations (부모)");
        System.out.println("   - recommended_course_items (자식)");
        System.out.println("🔍 DBeaver로 데이터 확인 가능\n");
    }
}
