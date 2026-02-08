package com.lxp.recommend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EngineProcessRequest(
        String batchId,
        String usersFilePath,
        String coursesFilePath,
        int topK,
        String callbackUrl
) {
}
