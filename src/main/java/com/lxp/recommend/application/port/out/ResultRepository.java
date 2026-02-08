package com.lxp.recommend.application.port.out;

import com.lxp.recommend.domain.result.entity.RecommendResult;

import java.util.Optional;

public interface ResultRepository {

    RecommendResult save(RecommendResult result);

    Optional<RecommendResult> findByUserId(String userId);

    void upsert(String userId, String courseIds, String batchId);

    boolean existsByUserId(String userId);
}
