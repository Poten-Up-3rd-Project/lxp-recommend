package com.lxp.recommend.infrastructure.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 전역 설정
 * 배치 메타데이터 관리 활성화
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    // Spring Batch 인프라 자동 구성
    // JobRepository, JobLauncher, JobExplorer 등 자동 생성
}
