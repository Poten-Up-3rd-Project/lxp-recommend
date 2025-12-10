package com.lxp.recommend.infrastructure.persistence.jpa.repository;

import com.lxp.recommend.infrastructure.persistence.jpa.entity.MemberRecommendationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberRecommendationRepository
        extends JpaRepository<MemberRecommendationJpaEntity, Long> {

    Optional<MemberRecommendationJpaEntity> findByMemberId(String memberId);
}
