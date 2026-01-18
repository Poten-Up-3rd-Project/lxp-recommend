package com.lxp.recommend.infrastructure.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 배치 수동 실행 컨트롤러 (로컬/개발 환경 전용)
 * 운영 환경에서는 비활성화됨
 */
@Slf4j
@RestController
@RequestMapping("/api-internal/batch")
@RequiredArgsConstructor
public class BatchManualTriggerController {

    private final JobLauncher jobLauncher;
    private final Job recommendationJob;

    /**
     * 추천 배치 수동 실행
     * 테스트용 엔드포인트
     *
     * @return 배치 실행 상태
     */
    @PostMapping("/recommendations/trigger")
    public ResponseEntity<?> triggerRecommendationBatch() {
        log.info("[Manual Trigger] Starting recommendation batch manually at {}", LocalDateTime.now());

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executionTime", LocalDateTime.now())
                    .addString("trigger", "manual")
                    .toJobParameters();

            jobLauncher.run(recommendationJob, jobParameters);

            log.info("[Manual Trigger] Batch job triggered successfully");

            return ResponseEntity.accepted().body(Map.of(
                    "status", "STARTED",
                    "message", "Recommendation batch job has been triggered successfully",
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("[Manual Trigger] Failed to trigger batch job", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "FAILED",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }
}
