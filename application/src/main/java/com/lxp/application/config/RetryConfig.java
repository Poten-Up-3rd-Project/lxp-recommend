package com.lxp.application.config;

import com.lxp.common.retry.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

    @Bean
    public RetryPolicy retryPolicy() {
        return new RetryPolicy() {
            @Override
            public int getMaxAttempts() {
                return 1;
            }

            @Override
            public long getBackoffMillis(int attempt) {
                return 0L;
            }

            @Override
            public boolean shouldRetry(Exception e) {
                return false;
            }
        };
    }
}