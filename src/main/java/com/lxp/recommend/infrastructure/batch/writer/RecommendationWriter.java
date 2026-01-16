package com.lxp.recommend.infrastructure.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * 추천 계산 완료 확인 Writer
 * Processor에서 이미 추천 계산 및 저장이 완료되었으므로
 * 성공한 학습자 ID 로깅만 수행
 */
@Slf4j
@Component
public class RecommendationWriter implements ItemWriter<String> {

    /**
     * Chunk 단위로 처리 완료된 학습자 ID 로깅
     *
     * @param chunk 처리 완료된 학습자 ID 목록 (Chunk 크기만큼)
     */
    @Override
    public void write(Chunk<? extends String> chunk) {
        if (chunk.isEmpty()) {
            return;
        }

        log.info("[Batch Writer] Successfully processed {} learners: {}",
                chunk.size(), chunk.getItems());
    }
}
