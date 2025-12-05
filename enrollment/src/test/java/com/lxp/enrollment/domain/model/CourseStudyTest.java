package com.lxp.enrollment.domain.model;

import com.lxp.enrollment.domain.model.enums.StudyStatus;
import com.lxp.enrollment.domain.model.vo.CourseStudyId;
import com.lxp.enrollment.domain.model.vo.LectureStudyId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 강좌 진도 계산 및 완료 판정 테스트
 */
@DisplayName("강좌 진도 계산 및 완료 판정")
class CourseStudyTest {

    private CourseStudy courseStudy;
    private LectureStudy lectureStudy1;
    private LectureStudy lectureStudy2;
    private LectureStudy lectureStudy3;

    @BeforeEach
    void setUp() {
        CourseStudyId courseStudyId = new CourseStudyId("learnerId_courseId");

        lectureStudy1 = LectureStudy.createLectureStudy(new LectureStudyId("learnerId_lectureId1"));
        lectureStudy2 = LectureStudy.createLectureStudy(new LectureStudyId("learnerId_lectureId2"));
        lectureStudy3 = LectureStudy.createLectureStudy(new LectureStudyId("learnerId_lectureId3"));

        courseStudy = CourseStudy.create(
                courseStudyId,
                List.of(lectureStudy1, lectureStudy2, lectureStudy3)
        );
    }

    @Test
    @DisplayName("새로 생성 된 강좌 학습 기록은 0% 진행률과 IN_PROGRESS 상태여야 한다")
    void shouldZeroPercentageAndInProgressStatus_WhenCreatedCourse() {
        assertEquals(0.0f, courseStudy.totalProgress(), "새로 생성된 강좌 학습 진행률은 0%여야 합니다.");
        assertEquals(StudyStatus.IN_PROGRESS, courseStudy.studyStatus(), "새로 생성된 강좌 상태는 IN_PROGRESS여야 한다.");
    }

    @Test
    @DisplayName("일부 강의만 완료했을 때, 강좌 진행률이 올바르게 계산되어야 한다.")
    void shouldCalculateCoursePercentage_WhenCompletedSomeLecture() {
        courseStudy.updateLectureProgress(lectureStudy1.lectureStudyId());
        courseStudy.updateLectureProgress(lectureStudy2.lectureStudyId());

        assertEquals(66.0f, courseStudy.totalProgress(),"일부 강의가 완료되었을 때, 강좌 진행률이 올바르게 계산되어야 합니다.");
    }

    @Test
    @DisplayName("모든 강의가 100% 완료 시 강좌가 자동으로 완료되어야 한다.")
    void shouldCompleteCourse_WhenWholeLectureCompleted() {
        courseStudy.updateLectureProgress(lectureStudy1.lectureStudyId());
        courseStudy.updateLectureProgress(lectureStudy2.lectureStudyId());
        courseStudy.updateLectureProgress(lectureStudy3.lectureStudyId());

        assertEquals(StudyStatus.COMPLETED, courseStudy.studyStatus(), "모든 강의가 완료되면 강좌가 완료 상태가 되어야 합니다.");
        assertNotNull(courseStudy.completedAt(), "강좌가 완료되면 완료된 시간이 표시되어야 합니다.");
    }

    @Test
    @DisplayName("완료 상태에서 다시 진도 업데이트를 시도해도 상태가 바뀌지 않아야 한다")
    void shouldNotChangeCourseStatus_WhenUpdateProgressWithStatusIsCompleted() {
        courseStudy.updateLectureProgress(lectureStudy1.lectureStudyId());
        courseStudy.updateLectureProgress(lectureStudy2.lectureStudyId());
        courseStudy.updateLectureProgress(lectureStudy3.lectureStudyId());

        assertThrows(IllegalStateException.class, () -> courseStudy.updateLectureProgress(lectureStudy3.lectureStudyId()));

    }

}