package com.lxp.recommend.adapter.in.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job recommendExportJob;

    @Scheduled(cron = "${batch.schedule.cron}")
    public void runRecommendBatch() {
        String batchId = generateBatchId();
        log.info("Starting scheduled batch: {}", batchId);

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("batchId", batchId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(recommendExportJob, params);
            log.info("Batch job started successfully: {}", batchId);
        } catch (Exception e) {
            log.error("Failed to start batch job: {}", batchId, e);
        }
    }

    public void runManualBatch() {
        String batchId = generateBatchId();
        log.info("Starting manual batch: {}", batchId);

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("batchId", batchId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(recommendExportJob, params);
            log.info("Manual batch job started successfully: {}", batchId);
        } catch (Exception e) {
            log.error("Failed to start manual batch job: {}", batchId, e);
            throw new RuntimeException("Failed to start batch job", e);
        }
    }

    private String generateBatchId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        return "batch_" + now.format(formatter);
    }
}
