package com.lxp.recommend.adapter.in.web;

import com.lxp.recommend.application.port.in.RecommendResultUseCase;
import com.lxp.recommend.dto.response.EngineCallbackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/sync")
@RequiredArgsConstructor
public class InternalSyncController {

    private final RecommendResultUseCase recommendResultUseCase;

    @PostMapping
    public ResponseEntity<Map<String, String>> handleCallback(@RequestBody EngineCallbackResponse callback) {
        log.info("Received callback for batch: {}, status: {}", callback.batchId(), callback.status());

        recommendResultUseCase.processCallback(callback);

        return ResponseEntity.ok(Map.of(
                "status", "RECEIVED",
                "batchId", callback.batchId()
        ));
    }
}
