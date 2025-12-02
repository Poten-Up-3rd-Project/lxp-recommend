package com.lxp.recommend.domain.repository;


import com.lxp.recommend.domain.model.MemberRecommendation;

import java.util.Optional;
import java.util.UUID;

public interface MemberRecommendationRepository {
    Optional<MemberRecommendation> findByMemberId(UUID memberId);
    MemberRecommendation save(MemberRecommendation recommendation);
    // 필요하다면 delete 등 추가
}