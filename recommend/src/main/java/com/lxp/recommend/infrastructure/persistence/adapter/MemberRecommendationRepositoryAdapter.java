package com.lxp.recommend.infrastructure.persistence.adapter;

import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.domain.repository.MemberRecommendationRepository;
import com.lxp.recommend.infrastructure.persistence.jpa.entity.RecommendedCourseItemJpaEntity;
import com.lxp.recommend.infrastructure.persistence.jpa.mapper.MemberRecommendationMapper;
import com.lxp.recommend.infrastructure.persistence.jpa.repository.JpaMemberRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRecommendationRepositoryAdapter implements MemberRecommendationRepository {

    private final JpaMemberRecommendationRepository jpaRepository;

    @Override
    public Optional<MemberRecommendation> findByMemberId(MemberId memberId) {
        return jpaRepository.findByMemberId(memberId.getValue())
                .map(MemberRecommendationMapper::toDomain);
    }

    @Override
    public MemberRecommendation save(MemberRecommendation domain) {
        // 1. 기존 엔티티가 있는지 확인
        var existingEntityOpt = jpaRepository.findByMemberId(domain.getMemberId().getValue());

        if (existingEntityOpt.isPresent()) {
            // 기존 데이터 업데이트 (UPDATE)
            var existingEntity = existingEntityOpt.get();

            // 새로운 추천 아이템들로 교체
            var newItems = domain.getItems().stream()
                    .map(course -> RecommendedCourseItemJpaEntity.of(
                            course.getCourseId().getValue(),
                            course.getScore(),
                            course.getRank()
                    ))
                    .collect(Collectors.toList());

            existingEntity.updateItems(newItems); // JPA 엔티티의 업데이트 메서드 호출

            var savedEntity = jpaRepository.save(existingEntity);
            return MemberRecommendationMapper.toDomain(savedEntity);

        } else {
            // 새 데이터 생성 (INSERT)
            var newEntity = MemberRecommendationMapper.toEntity(domain);
            var savedEntity = jpaRepository.save(newEntity);
            return MemberRecommendationMapper.toDomain(savedEntity);
        }
    }
}
