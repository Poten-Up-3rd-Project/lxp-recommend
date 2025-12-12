package com.lxp.enrollment.application.event;

import com.lxp.enrollment.domain.event.ProgressCreatedEvent;
import com.lxp.enrollment.domain.event.ProgressDeletedEvent;
import com.lxp.enrollment.domain.model.CourseProgress;
import com.lxp.enrollment.domain.model.LectureProgress;
import com.lxp.enrollment.domain.model.vo.LectureId;
import com.lxp.enrollment.domain.repository.CourseProgressRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * CourseProgressEvent 처리 핸들러
 */
@Component
public class CourseProgressEventHandler {

    private final CourseProgressRepository courseProgressRepository;

    public CourseProgressEventHandler(CourseProgressRepository courseProgressRepository) {
        this.courseProgressRepository = courseProgressRepository;
    }

    /**
     * CourseProgress 생성 이벤트 처리
     * @param event 진행률 생성 이벤트
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourseProgressCreated(ProgressCreatedEvent event) {
        //TODO : 강의 목록을 외부에서 조회해 와서 반영해야 함
        CourseProgress courseProgress = CourseProgress.create(event.userId(), event.courseId(), List.of("1", "2").stream()
            .map(id ->
                LectureProgress.create(
                        event.userId(),
                        LectureId.from(id))
            ).toList());

        courseProgressRepository.save(courseProgress);
    }

    /**
     * CourseStudy 삭제 이벤트 처리
     * @param event 진행률 삭제 이벤트
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourseProgressDeleted(ProgressDeletedEvent event) {;
        CourseProgress courseProgress = courseProgressRepository.findByUserIdAndCourseId(event.userId(), event.courseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 강좌 진행률을 찾을 수 없습니다. " +
                        "UserId: " + event.userId().value() + ", CourseId: " + event.courseId().value()));

        courseProgressRepository.delete(courseProgress);
    }

}
