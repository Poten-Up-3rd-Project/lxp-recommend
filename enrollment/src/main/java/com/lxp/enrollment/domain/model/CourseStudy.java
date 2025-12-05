package com.lxp.enrollment.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.enrollment.domain.model.enums.StudyStatus;
import com.lxp.enrollment.domain.model.vo.CourseStudyId;
import com.lxp.enrollment.domain.model.vo.LectureStudyId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 강좌 진행률 도메인
 */
public class CourseStudy extends AggregateRoot {

    private CourseStudyId courseStudyId;
    private float totalProgress;
    private StudyStatus studyStatus;
    private OffsetDateTime completedAt;

    private final List<LectureStudy> lectureStudies;

    /**
     * 강좌 진행률 생성
     * @param courseStudyId 강좌 진행률 ID(학습자ID + '_' + 강좌 ID)
     * @param lectureStudies 강의 진행률 리스트
     * @return 생성된 강좌 진행률
     */
    public static CourseStudy create(CourseStudyId courseStudyId, List<LectureStudy> lectureStudies) {
        return new CourseStudy(
                Objects.requireNonNull(courseStudyId, "CourseStudyId는 null일 수 없습니다."),
                StudyStatus.IN_PROGRESS,
                0.0f,
                Objects.requireNonNull(lectureStudies, "LectureStudies는 null일 수 없습니다."),
                null
        );
    }

    /**
     * 강의 진행상태 업데이트
     */
    public void updateLectureProgress(LectureStudyId id) {
        if(this.studyStatus == StudyStatus.COMPLETED) {
            throw new IllegalStateException("완료 상태의 강의는 진도를 업데이트 할 수 없습니다.");
        }

        LectureStudy lectureStudy = lectureStudies.stream()
            .filter(lecStd -> lecStd.lectureStudyId().equals(id))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당 LectureStudyID에 해당하는 LectureStudy가 없습니다. : " + id.value()));

        if(lectureStudy.completed())
            return;

        lectureStudy.changeCompleted();

        recalculateProgress();
    }

    /**
     * 강좌 진행률의 진행률 재 계산
     * 소수점 버림
     */
    private void recalculateProgress() {
        float total = ((float) lectureStudies.stream().filter(LectureStudy::completed).count()) /
                lectureStudies.size() * 100;

        this.totalProgress = BigDecimal.valueOf(total).setScale(0, RoundingMode.FLOOR).floatValue();

        determineCompletion();
    }

    /**
     * 강좌 완료 처리
     */
    private void determineCompletion() {
        if (this.totalProgress == 100) {
            this.studyStatus = StudyStatus.COMPLETED;
            this.completedAt = OffsetDateTime.now();
        }
    }

    public CourseStudyId courseStudyId() {
        return courseStudyId;
    }

    public float totalProgress() {
        return totalProgress;
    }

    public StudyStatus studyStatus() {
        return studyStatus;
    }

    public OffsetDateTime completedAt() {
        return completedAt;
    }

    public List<LectureStudy> lectureStudies() {
        return lectureStudies;
    }

    private CourseStudy(CourseStudyId courseStudyId, StudyStatus studyStatus, float totalProgress, List<LectureStudy> lectureStudies, OffsetDateTime completedAt) {
        this.courseStudyId = courseStudyId;
        this.studyStatus = studyStatus;
        this.totalProgress = totalProgress;
        this.lectureStudies = lectureStudies;
        this.completedAt = completedAt;
    }

}
