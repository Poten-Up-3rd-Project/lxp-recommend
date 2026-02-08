package com.lxp.recommend.adapter.out.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.recommend.application.port.out.RecommendEnginePort;
import com.lxp.recommend.dto.request.EngineProcessRequest;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendEngineAdapter implements RecommendEnginePort {

    @Value("${engine.base-url}")
    private String engineBaseUrl;

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public void requestProcess(String batchId, String usersFilePath, String coursesFilePath, int topK, String callbackUrl) {
        EngineProcessRequest request = new EngineProcessRequest(
                batchId,
                usersFilePath,
                coursesFilePath,
                topK,
                callbackUrl
        );

        try {
            log.debug("Engine request body: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize request for logging", e);
        }

        try {
            RestClient restClient = restClientBuilder.baseUrl(engineBaseUrl).build();

            restClient.post()
                    .uri("/engine/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully called engine for batch: {}", batchId);
        } catch (Exception e) {
            log.error("Failed to call recommend engine for batch: {}", batchId, e);
            throw new BusinessException(ErrorCode.ENGINE_CALL_FAILED, e);
        }
    }
}
