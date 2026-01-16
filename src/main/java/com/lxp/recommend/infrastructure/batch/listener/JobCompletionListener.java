package com.lxp.recommend.infrastructure.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 배치 Job 완료 리스너
 * Job 시작/종료 시점에 로깅 및 통계 수집
 */
@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║       추천 배치 작업 시작                                ║");
        log.info("╚════════════════════════════════════════════════════════╝");
        log.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job ID: {}", jobExecution.getJobId());
        log.info("Start Time: {}", jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );

        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║       추천 배치 작업 완료                                ║");
        log.info("╚════════════════════════════════════════════════════════╝");
        log.info("Status: {}", jobExecution.getStatus());
        log.info("Duration: {}분 {}초", duration.toMinutes(), duration.getSeconds() % 60);

        if (!jobExecution.getStepExecutions().isEmpty()) {
            var stepExecution = jobExecution.getStepExecutions().iterator().next();
            log.info("Read Count: {}", stepExecution.getReadCount());
            log.info("Write Count: {}", stepExecution.getWriteCount());
            log.info("Skip Count: {}", stepExecution.getSkipCount());
        }

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("✅ 배치 작업 정상 완료");
        } else {
            log.error("❌ 배치 작업 실패: {}", jobExecution.getAllFailureExceptions());
        }
    }
}
