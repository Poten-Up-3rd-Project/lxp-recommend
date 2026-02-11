package com.lxp.recommend.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.recommend.adapter.in.web.InternalSyncController;
import com.lxp.recommend.application.port.in.RecommendResultUseCase;
import com.lxp.recommend.dto.response.EngineCallbackResponse;
import com.lxp.recommend.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InternalSyncControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RecommendResultUseCase recommendResultUseCase;

    @InjectMocks
    private InternalSyncController internalSyncController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(internalSyncController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("성공 콜백을 수신할 수 있다")
    void handleCallback_completed() throws Exception {
        EngineCallbackResponse callback = new EngineCallbackResponse(
                "batch_20260207_0600",
                "COMPLETED",
                "results/2026/02/07/batch_20260207_0600/recommendations.parquet",
                1523,
                LocalDateTime.of(2026, 2, 7, 6, 15, 30),
                null,
                null,
                null
        );

        willDoNothing().given(recommendResultUseCase).processCallback(any());

        mockMvc.perform(post("/internal/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andExpect(jsonPath("$.batchId").value("batch_20260207_0600"));

        then(recommendResultUseCase).should().processCallback(any(EngineCallbackResponse.class));
    }

    @Test
    @DisplayName("실패 콜백을 수신할 수 있다")
    void handleCallback_failed() throws Exception {
        EngineCallbackResponse callback = new EngineCallbackResponse(
                "batch_20260207_0600",
                "FAILED",
                null,
                null,
                null,
                "SCORING_ERROR",
                "TF-IDF matrix computation failed",
                LocalDateTime.of(2026, 2, 7, 6, 15, 30)
        );

        willDoNothing().given(recommendResultUseCase).processCallback(any());

        mockMvc.perform(post("/internal/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));

        then(recommendResultUseCase).should().processCallback(any(EngineCallbackResponse.class));
    }
}
