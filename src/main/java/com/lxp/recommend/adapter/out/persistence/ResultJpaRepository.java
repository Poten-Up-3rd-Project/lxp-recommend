package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.result.entity.RecommendResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResultJpaRepository extends JpaRepository<RecommendResult, Long> {

    Optional<RecommendResult> findByUserId(String userId);

    boolean existsByUserId(String userId);

    @Modifying
    @Query(value = """
            INSERT INTO recommend_result (user_id, course_ids, batch_id, created_at, updated_at)
            VALUES (:userId, :courseIds, :batchId, NOW(), NOW())
            ON DUPLICATE KEY UPDATE course_ids = :courseIds, batch_id = :batchId, updated_at = NOW()
            """, nativeQuery = true)
    void upsert(@Param("userId") String userId,
                @Param("courseIds") String courseIds,
                @Param("batchId") String batchId);
}
