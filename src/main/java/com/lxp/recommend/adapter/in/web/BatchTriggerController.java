package com.lxp.recommend.adapter.in.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api-v1/recommend/public/batch")
@RequiredArgsConstructor
public class BatchTriggerController {

    private final JobLauncher jobLauncher;
    private final Job recommendExportJob;

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerBatch() {
        try {
            String batchId = generateBatchId();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("batchId", batchId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Manually triggering batch job with batchId: {}", batchId);

            jobLauncher.run(recommendExportJob, jobParameters);

            return ResponseEntity.ok(Map.of(
                    "status", "STARTED",
                    "batchId", batchId,
                    "message", "Batch job started successfully"
            ));
        } catch (Exception e) {
            log.error("Failed to trigger batch job", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "FAILED",
                    "message", e.getMessage()
            ));
        }
    }

    private String generateBatchId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        return "batch_" + now.format(formatter);
    }
}
