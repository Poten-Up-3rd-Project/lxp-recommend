package com.lxp.enrollment.domain.repository;

import com.lxp.enrollment.domain.model.CourseProgress;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.UserId;

import java.util.Optional;

/**
 * 강좌 진행률 리포지토리
 */
public interface CourseProgressRepository {

    Optional<CourseProgress> findByUserIdAndCourseId(UserId userId, CourseId courseId);

    CourseProgress save(CourseProgress courseProgress);

    void delete(CourseProgress courseProgress);

}
