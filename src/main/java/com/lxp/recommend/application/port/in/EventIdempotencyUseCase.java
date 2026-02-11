package com.lxp.recommend.application.port.in;

public interface EventIdempotencyUseCase {

    /**
     * 이미 처리된 이벤트인지 확인합니다.
     * @return true면 이미 처리됨 (skip 해야 함)
     */
    boolean isDuplicate(String eventId);

    void markAsProcessed(String eventId);
}
