package com.lxp.enrollment.infrastructure.persistence.repository;

import com.lxp.enrollment.domain.model.CourseProgress;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;
import com.lxp.enrollment.domain.repository.CourseProgressRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 강좌 진행률 퍼시스턴스 어댑터
 */
@Component
public class CourseProgressPersistenceAdapter implements CourseProgressRepository {

    private final JpaCourseProgressRepository jpaCourseProgressRepository;

    public CourseProgressPersistenceAdapter(JpaCourseProgressRepository jpaCourseProgressRepository) {
        this.jpaCourseProgressRepository = jpaCourseProgressRepository;
    }

    @Override
    public Optional<CourseProgress> findByUserIdAndCourseId(UserId userId, CourseId courseId) {
        return Optional.empty();
    }

    @Override
    public CourseProgress save(CourseProgress courseProgress) {
        return null;
    }

    @Override
    public void delete(CourseProgress courseProgress) {

    }

}
