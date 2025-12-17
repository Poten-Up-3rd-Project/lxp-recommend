package com.lxp.recommend.application.mapper;

import com.lxp.recommend.application.dto.RecommendedCourseDto;
import com.lxp.recommend.domain.model.RecommendedCourse;
import org.springframework.stereotype.Component;

/**
 * RecommendedCourse Domain → DTO 변환
 */
@Component
public class RecommendedCourseMapper {

    /**
     * Domain 객체 → DTO 변환
     */
    public RecommendedCourseDto toDto(RecommendedCourse course) {
        return new RecommendedCourseDto(
                course.getCourseId().getValue(),
                course.getScore(),
                course.getRank()
        );
    }
}
