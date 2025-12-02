package com.lxp.recommend.infrastructure.persistence;

import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaMemberRecommendationRepository
        extends JpaRepository<MemberRecommendation, UUID>, MemberRecommendationRepository {

    // MemberRecommendationRepository의 메서드 시그니처와 JpaRepository가 호환되므로
    // 별도 구현 없이 바로 동작합니다.
}