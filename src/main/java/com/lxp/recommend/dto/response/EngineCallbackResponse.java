package com.lxp.recommend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record EngineCallbackResponse(
        @JsonProperty("batch_id") String batchId,
        String status,
        @JsonProperty("result_file_path") String resultFilePath,
        @JsonProperty("user_count") Integer userCount,
        @JsonProperty("processed_at") LocalDateTime processedAt,
        @JsonProperty("error_code") String errorCode,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("failed_at") LocalDateTime failedAt
) {
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isFailed() {
        return "FAILED".equals(status);
    }
}
