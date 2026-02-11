package com.lxp.recommend.adapter.in.batch.job;

import com.lxp.recommend.adapter.in.batch.tasklet.FastApiCallTasklet;
import com.lxp.recommend.adapter.in.batch.tasklet.R2UploadTasklet;
import com.lxp.recommend.adapter.in.batch.tasklet.UserCourseExportTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RecommendBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserCourseExportTasklet userCourseExportTasklet;
    private final R2UploadTasklet r2UploadTasklet;
    private final FastApiCallTasklet fastApiCallTasklet;

    @Bean
    public Job recommendExportJob() {
        return new JobBuilder("recommendExportJob", jobRepository)
                .start(exportStep())
                .next(uploadStep())
                .next(engineCallStep())
                .build();
    }

    @Bean
    public Step exportStep() {
        return new StepBuilder("exportStep", jobRepository)
                .tasklet(userCourseExportTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step uploadStep() {
        return new StepBuilder("uploadStep", jobRepository)
                .tasklet(r2UploadTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step engineCallStep() {
        return new StepBuilder("engineCallStep", jobRepository)
                .tasklet(fastApiCallTasklet, transactionManager)
                .build();
    }
}
