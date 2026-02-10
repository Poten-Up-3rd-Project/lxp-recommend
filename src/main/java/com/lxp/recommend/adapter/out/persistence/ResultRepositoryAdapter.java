package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.result.entity.RecommendResult;
import com.lxp.recommend.application.port.out.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResultRepositoryAdapter implements ResultRepository {

    private final ResultJpaRepository resultJpaRepository;

    @Override
    public RecommendResult save(RecommendResult result) {
        return resultJpaRepository.save(result);
    }

    @Override
    public Optional<RecommendResult> findByUserId(String userId) {
        return resultJpaRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void upsert(String userId, String courseIds, String batchId) {
        resultJpaRepository.upsert(userId, courseIds, batchId);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return resultJpaRepository.existsByUserId(userId);
    }
}
