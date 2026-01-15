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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 추천 데이터 고급 시나리오 테스트
 * - 다중 사용자
 * - 덮어쓰기
 * - 예외 처리
 */
@SpringBootTest
@ActiveProfiles("persistence")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecommendPersistenceAdvancedTest {

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
    @DisplayName("[고급-1] 다중 사용자 추천 저장")
    void saveMultipleUsersRecommendation() {
        // Given
        List<String> userIds = List.of(
                "user-junior",
                "user-middle",
                "user-senior"
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

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [고급-1] 다중 사용자 추천 저장         ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // When
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            String level = levels.get(i);
            System.out.printf("📌 사용자 %d: %s (레벨: %s)%n", i + 1, userId, level);
            commandService.refreshRecommendation(userId);
        }

        // Then
        List<MemberRecommendationJpaEntity> allRecs = repository.findAll();

        System.out.println("\n✅ 총 저장된 회원 추천: " + allRecs.size() + "명\n");

        allRecs.forEach(rec -> {
            System.out.printf("  %-25s : %d개 추천%n",
                    rec.getMemberId(),
                    rec.getItems().size());
        });

        System.out.println("\n════════════════════════════════════════════\n");

        assertThat(allRecs).hasSize(3);
        userIds.forEach(userId -> {
            assertThat(allRecs).anyMatch(rec -> rec.getMemberId().equals(userId));
        });
    }

    @Test
    @Order(2)
    @DisplayName("[고급-2] 추천 덮어쓰기 확인 (UPDATE)")
    void verifyRecommendationOverwrite() {
        // Given
        String learnerId = "user-overwrite";
        mockValidUserData(learnerId);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [고급-2] 추천 덮어쓰기 확인            ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // When: 1차 저장
        System.out.println("📌 1차 추천 실행...");
        commandService.refreshRecommendation(learnerId);

        Optional<MemberRecommendationJpaEntity> firstRec = repository.findByMemberId(learnerId);
        assertThat(firstRec).isPresent();
        int firstCount = firstRec.get().getItems().size();
        Long firstRecId = firstRec.get().getId();

        System.out.println("✅ 1차 저장 완료: " + firstCount + "개 (ID: " + firstRecId + ")\n");

        // When: 2차 저장
        System.out.println("📌 2차 추천 실행 (덮어쓰기)...");
        commandService.refreshRecommendation(learnerId);

        Optional<MemberRecommendationJpaEntity> secondRec = repository.findByMemberId(learnerId);
        assertThat(secondRec).isPresent();
        int secondCount = secondRec.get().getItems().size();
        Long secondRecId = secondRec.get().getId();

        System.out.println("✅ 2차 저장 완료: " + secondCount + "개 (ID: " + secondRecId + ")\n");

        System.out.println("════════════════════════════════════════════");
        System.out.printf("  1차 저장: %d개 (ID: %d)%n", firstCount, firstRecId);
        System.out.printf("  2차 저장: %d개 (ID: %d)%n", secondCount, secondRecId);
        System.out.println("  결과: " + (firstRecId.equals(secondRecId) ? "UPDATE" : "재생성"));
        System.out.println("════════════════════════════════════════════\n");

        // Then: 여전히 1명만 존재 (덮어쓰기)
        List<MemberRecommendationJpaEntity> allRecs = repository.findAll();
        assertThat(allRecs).hasSize(1);
        assertThat(allRecs.get(0).getMemberId()).isEqualTo(learnerId);
        assertThat(firstRecId).isEqualTo(secondRecId);  // 같은 ID = UPDATE
    }

    @Test
    @Order(3)
    @DisplayName("[고급-3] 프로필 없는 사용자 예외 처리")
    void verifyExceptionWhenNoProfile() {
        // Given
        String learnerId = "user-no-profile";

        when(learnerProfilePort.getProfile(learnerId))
                .thenReturn(Optional.empty());

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     [고급-3] 예외 처리 확인                ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        System.out.println("📌 프로필 없는 사용자: " + learnerId);

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> commandService.refreshRecommendation(learnerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("학습자 프로필을 찾을 수 없습니다");

        // Then: DB에 저장되지 않음
        Optional<MemberRecommendationJpaEntity> savedRec = repository.findByMemberId(learnerId);

        System.out.println("✅ 예외 발생 확인");
        System.out.println("✅ DB에 저장되지 않음: " + savedRec.isEmpty() + "\n");

        assertThat(savedRec).isEmpty();
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║         [고급 테스트 완료]                 ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("✅ 고급 시나리오 테스트 완료\n");
    }

    // Helper
    private void mockValidUserData(String learnerId) {
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
    }
}
