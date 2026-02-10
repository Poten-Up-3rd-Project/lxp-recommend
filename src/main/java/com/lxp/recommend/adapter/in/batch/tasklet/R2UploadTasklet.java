package com.lxp.recommend.adapter.in.batch.tasklet;

import com.lxp.recommend.application.port.out.StoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class R2UploadTasklet implements Tasklet {

    private final StoragePort storagePort;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String batchId = (String) chunkContext.getStepContext()
                .getJobParameters().get("batchId");

        String usersParquetPath = (String) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext()
                .get("usersParquetPath");

        String coursesParquetPath = (String) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext()
                .get("coursesParquetPath");

        log.info("Starting R2 upload for batch: {}", batchId);

        LocalDateTime now = LocalDateTime.now();
        String basePath = buildBasePath(now, batchId);

        String usersKey = basePath + "/users.parquet";
        String coursesKey = basePath + "/courses.parquet";

        Path usersPath = Path.of(usersParquetPath);
        Path coursesPath = Path.of(coursesParquetPath);

        storagePort.upload(usersKey, usersPath);
        storagePort.upload(coursesKey, coursesPath);

        Files.deleteIfExists(usersPath);
        Files.deleteIfExists(coursesPath);

        chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext()
                .put("usersR2Key", usersKey);
        chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext()
                .put("coursesR2Key", coursesKey);

        log.info("R2 upload completed. Users: {}, Courses: {}", usersKey, coursesKey);

        return RepeatStatus.FINISHED;
    }

    private String buildBasePath(LocalDateTime dateTime, String batchId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return "exports/" + dateTime.format(formatter) + "/" + batchId;
    }
}
