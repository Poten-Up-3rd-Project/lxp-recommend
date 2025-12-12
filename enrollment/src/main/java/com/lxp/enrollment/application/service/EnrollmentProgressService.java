package com.lxp.enrollment.application.service;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.enrollment.domain.event.ProgressCompletedEvent;
import com.lxp.enrollment.domain.model.CourseProgress;
import com.lxp.enrollment.domain.model.LectureProgress;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.LectureId;
import com.lxp.enrollment.domain.model.vo.UserId;

import com.lxp.enrollment.domain.repository.CourseProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 강좌 진행률 도메인 서비스
 */
@Service
public class EnrollmentProgressService {

    private final CourseProgressRepository enrollmentProgressRepository;
    private final DomainEventPublisher domainEventPublisher;

    public EnrollmentProgressService(CourseProgressRepository enrollmentProgressRepository, DomainEventPublisher domainEventPublisher) {
        this.enrollmentProgressRepository = enrollmentProgressRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * 강좌 진행률 업데이트
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @param lectureId 강의 ID
     */
    public void updateProgress(UserId userId, CourseId courseId, LectureId lectureId) {
        CourseProgress courseProgress = enrollmentProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 강좌 진행률을 찾을 수 없습니다. UserId: " + userId.value() + ", CourseId: " + courseId.value()));

        courseProgress.updateProgress(lectureId);

        if(courseProgress.isCompleted()) {
            domainEventPublisher.publish(ProgressCompletedEvent.of(courseProgress));
        }
    }

    /**
     * 강좌의 모든 강의 진행상태 조회
     * @param userId
     * @param courseId
     * @return
     */
    public List<LectureProgress> getLectureProgresses(UserId userId, CourseId courseId) {
        CourseProgress courseProgress = enrollmentProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 강좌 진행률을 찾을 수 없습니다. UserId: " + userId.value() + ", CourseId: " + courseId.value()));

        return courseProgress.lectureProgresses();
    }
}
