package com.lxp.recommend.domain.repository;

import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.ids.MemberId;

import java.util.Optional;

public interface MemberRecommendationRepository {
    Optional<MemberRecommendation> findByMemberId(MemberId memberId);
    MemberRecommendation save(MemberRecommendation recommendation);
}
