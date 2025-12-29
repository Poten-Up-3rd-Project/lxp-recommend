package com.lxp.recommend.infrastructure.persistence.mapper;

import com.lxp.recommend.domain.model.MemberRecommendation;
import com.lxp.recommend.domain.model.RecommendedCourse;
import com.lxp.recommend.domain.model.ids.CourseId;
import com.lxp.recommend.domain.model.ids.MemberId;
import com.lxp.recommend.infrastructure.persistence.entity.MemberRecommendationJpaEntity;
import com.lxp.recommend.infrastructure.persistence.entity.RecommendedCourseItemJpaEntity;

import java.util.stream.Collectors;

public class MemberRecommendationMapper {

    // Domain → JPA Entity
    public static MemberRecommendationJpaEntity toEntity(MemberRecommendation domain) {
        var items = domain.getItems().stream()
                .map(course -> RecommendedCourseItemJpaEntity.of(
                        course.getCourseId().getValue(),
                        course.getScore(),
                        course.getRank()
                ))
                .collect(Collectors.toList());

        return MemberRecommendationJpaEntity.of(
                domain.getMemberId().getValue(),
                items,
                domain.getCalculatedAt()
        );
    }

    // JPA Entity → Domain
    public static MemberRecommendation toDomain(MemberRecommendationJpaEntity entity) {
        var items = entity.getItems().stream()
                .map(item -> new RecommendedCourse(
                        CourseId.of(item.getCourseId()),
                        item.getScore(),
                        item.getRank()
                ))
                .collect(Collectors.toList());

        return MemberRecommendation.reconstruct(
                entity.getId(),
                MemberId.of(entity.getMemberId()),
                items,
                entity.getCalculatedAt()
        );
    }
}
