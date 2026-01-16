package com.lxp.recommend.infrastructure.batch.config;

import com.lxp.recommend.infrastructure.batch.listener.JobCompletionListener;
import com.lxp.recommend.infrastructure.batch.processor.RecommendationProcessor;
import com.lxp.recommend.infrastructure.batch.reader.LearnerIdReader;
import com.lxp.recommend.infrastructure.batch.writer.RecommendationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 추천 배치 Job 설정
 * Job: recommendationJob
 * Step: recommendationStep (Chunk 기반 처리)
 */
@Configuration
@RequiredArgsConstructor
public class RecommendationBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final LearnerIdReader learnerIdReader;
    private final RecommendationProcessor recommendationProcessor;
    private final RecommendationWriter recommendationWriter;
    private final JobCompletionListener jobCompletionListener;

    @Value("${batch.recommendation.chunk-size:10}")
    private int chunkSize;

    /**
     * 추천 배치 Job 정의
     * 매일 새벽 3시 실행되는 전체 학습자 추천 갱신 작업
     */
    @Bean
    public Job recommendationJob() {
        return new JobBuilder("recommendationJob", jobRepository)
                .listener(jobCompletionListener)
                .start(recommendationStep())
                .build();
    }

    /**
     * 추천 계산 Step 정의
     * Chunk 기반: chunkSize만큼 read → process → write 반복
     *
     * 처리 흐름:
     * 1. Reader: 학습자 ID 10개 조회
     * 2. Processor: 각 학습자별 추천 계산 (10회)
     * 3. Writer: 성공한 학습자 ID 로깅 (1회)
     * 4. Commit: 트랜잭션 커밋
     */
    @Bean
    public Step recommendationStep() {
        return new StepBuilder("recommendationStep", jobRepository)
                .<String, String>chunk(chunkSize, transactionManager)
                .reader(learnerIdReader)
                .processor(recommendationProcessor)
                .writer(recommendationWriter)
                .build();
    }
}
