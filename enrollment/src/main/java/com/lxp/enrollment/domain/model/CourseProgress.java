package com.lxp.enrollment.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.enrollment.domain.model.enums.ProgressStatus;
import com.lxp.enrollment.domain.model.vo.CourseId;
import com.lxp.enrollment.domain.model.vo.CourseProgressId;
import com.lxp.enrollment.domain.model.vo.LectureId;
import com.lxp.enrollment.domain.model.vo.UserId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 강좌 진행률 도메인
 */
public class CourseProgress extends AggregateRoot<CourseProgressId> {

    private CourseProgressId courseProgressId;
    private UserId userId;
    private CourseId courseId;
    private float totalProgress;
    private ProgressStatus progressStatus;
    private OffsetDateTime completedAt;

    private final List<LectureProgress> lectureProgresses;

    /**
     * 강좌 진행률 생성
     * @param userId 학습자 ID
     * @param courseId 강좌 ID
     * @param lectureProgresses 강의 진행률 리스트
     * @return 생성된 강좌 진행률
     */
    public static CourseProgress create(UserId userId, CourseId courseId, List<LectureProgress> lectureProgresses) {
        return new CourseProgress(
                Objects.requireNonNull(userId, "UserId는 null일 수 없습니다."),
                Objects.requireNonNull(courseId, "CourseId는 null일 수 없습니다."),
                ProgressStatus.IN_PROGRESS,
                0.0f,
                Objects.requireNonNull(lectureProgresses, "LectureProgresses는 null일 수 없습니다."),
                null
        );
    }

    /**
     * 강의 진행상태 업데이트
     * @param id 강의 ID
     */
    public void updateProgress(LectureId id) {
        if(this.progressStatus == ProgressStatus.COMPLETED) {
            throw new IllegalStateException("완료 상태의 강의는 진도를 업데이트 할 수 없습니다.");
        }

        LectureProgress lectureProgress = lectureProgresses.stream()
            .filter(lecProgress -> lecProgress.lectureId().equals(id))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당 LectureProgressID에 해당하는 LectureProgress가 없습니다. : " + id.value()));

        if(lectureProgress.completed())
            return;

        lectureProgress.changeCompleted();

        recalculateProgress();
    }

    /**
     * 강좌 진행률 완료 여부
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return (this.progressStatus == ProgressStatus.COMPLETED && this.totalProgress == 100.0f);
    }

    /**
     * 강좌 진행률의 진행률 재 계산
     * 소수점 버림
     */
    private void recalculateProgress() {
        float total = ((float) lectureProgresses.stream().filter(LectureProgress::completed).count()) /
                lectureProgresses.size() * 100;

        this.totalProgress = BigDecimal.valueOf(total).setScale(0, RoundingMode.FLOOR).floatValue();

        determineCompletion();
    }

    /**
     * 강좌 완료 처리
     */
    private void determineCompletion() {
        if (this.totalProgress == 100) {
            this.progressStatus = ProgressStatus.COMPLETED;
            this.completedAt = OffsetDateTime.now();
        }
    }

    @Override
    public CourseProgressId getId() {
        return courseProgressId;
    }

    public UserId userId() { return userId;}

    public CourseId courseId() { return courseId; }

    public float totalProgress() {
        return totalProgress;
    }

    public ProgressStatus studyStatus() {
        return progressStatus;
    }

    public OffsetDateTime completedAt() {
        return completedAt;
    }

    public List<LectureProgress> lectureProgresses() {
        return lectureProgresses;
    }

    private CourseProgress(UserId userId, CourseId courseId, ProgressStatus progressStatus, float totalProgress, List<LectureProgress> lectureProgresses, OffsetDateTime completedAt) {
        this.lectureProgresses = lectureProgresses;
        this.completedAt = completedAt;
        this.progressStatus = progressStatus;
        this.totalProgress = totalProgress;
        this.courseId = courseId;
        this.userId = userId;
    }

    private CourseProgress(CourseProgressId courseProgressId, UserId userId, CourseId courseId, ProgressStatus progressStatus, float totalProgress, List<LectureProgress> lectureProgresses, OffsetDateTime completedAt) {
        this.courseProgressId = courseProgressId;
        this.userId = userId;
        this.courseId = courseId;
        this.progressStatus = progressStatus;
        this.totalProgress = totalProgress;
        this.lectureProgresses = lectureProgresses;
        this.completedAt = completedAt;
    }

}
