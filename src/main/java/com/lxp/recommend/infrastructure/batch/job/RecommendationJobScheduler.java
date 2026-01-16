package com.lxp.recommend.infrastructure.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 추천 배치 스케줄러
 * 매일 새벽 3시 자동 실행
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class RecommendationJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job recommendationJob;

    /**
     * 매일 새벽 3시 실행
     * Cron: 초(0) 분(0) 시(3) 일(*) 월(*) 요일(*)
     *
     * 테스트용으로 임시 변경 가능:
     * - 매분 실행: "0 * * * * *"
     * - 5분마다: "
     *
     * */

    @Scheduled(cron = "0 0 3 * * *")
    public void runRecommendationBatch() {
        log.info("[Scheduler] Starting recommendation batch at {}", LocalDateTime.now());

        try {
            // JobParameters에 실행 시간 추가 (중복 실행 방지)
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executionTime", LocalDateTime.now())
                    .toJobParameters();

            // Job 실행
            jobLauncher.run(recommendationJob, jobParameters);

            log.info("[Scheduler] Recommendation batch completed successfully");

        } catch (Exception e) {
            log.error("[Scheduler] Failed to run recommendation batch", e);
        }
    }
}
