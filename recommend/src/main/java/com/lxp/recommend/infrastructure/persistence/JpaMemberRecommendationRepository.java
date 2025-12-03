package com.lxp.recommend.infrastructure.persistence;

import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaMemberRecommendationRepository
        extends JpaRepository<MemberRecommendation, Long>, MemberRecommendationRepository {

    // Spring Data JPA가 Embedded 타입인 MemberId로 자동 검색 지원
    Optional<MemberRecommendation> findByMemberId(MemberId memberId);
}
