package com.lxp.recommend.adapter.in.batch.tasklet;

import com.lxp.recommend.application.port.out.RecommendEnginePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiCallTasklet implements Tasklet {

    private final RecommendEnginePort recommendEnginePort;

    @Value("${engine.callback-url}")
    private String callbackUrl;

    @Value("${engine.top-k}")
    private int topK;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String batchId = (String) chunkContext.getStepContext()
                .getJobParameters().get("batchId");

        String usersKey = (String) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext()
                .get("usersR2Key");

        String coursesKey = (String) chunkContext.getStepContext()
                .getStepExecution().getJobExecution().getExecutionContext()
                .get("coursesR2Key");

        log.info("Calling FastAPI engine for batch: {}, usersKey: {}, coursesKey: {}", batchId, usersKey, coursesKey);

        recommendEnginePort.requestProcess(batchId, usersKey, coursesKey, topK, callbackUrl);

        log.info("FastAPI engine call completed for batch: {}", batchId);

        return RepeatStatus.FINISHED;
    }
}
