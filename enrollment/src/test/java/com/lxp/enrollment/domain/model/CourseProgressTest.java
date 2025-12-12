package com.lxp.enrollment.domain.model;

import com.lxp.enrollment.domain.model.enums.ProgressStatus;
import com.lxp.enrollment.domain.model.vo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 강좌 진도 계산 및 완료 판정 테스트
 */
@DisplayName("강좌 진도 계산 및 완료 판정")
class CourseProgressTest {

    private CourseProgress courseProgress;
    private LectureProgress lectureProgress1;
    private LectureProgress lectureProgress2;
    private LectureProgress lectureProgress3;

    @BeforeEach
    void setUp() {
        CourseId courseId = new CourseId(UUID.randomUUID().toString());
        UserId userId = new UserId(UUID.randomUUID().toString());

        LectureId lectureId1 = new LectureId(UUID.randomUUID().toString());
        LectureId lectureId2 = new LectureId(UUID.randomUUID().toString());
        LectureId lectureId3 = new LectureId(UUID.randomUUID().toString());

        lectureProgress1 = LectureProgress.create(userId, lectureId1);
        lectureProgress2 = LectureProgress.create(userId, lectureId2);
        lectureProgress3 = LectureProgress.create(userId, lectureId3);

        courseProgress = CourseProgress.create(
                userId,
                courseId,
                List.of(lectureProgress1, lectureProgress2, lectureProgress3)
        );
    }

    @Test
    @DisplayName("새로 생성 된 강좌 학습 기록은 0% 진행률과 IN_PROGRESS 상태여야 한다")
    void shouldZeroPercentageAndInProgressStatus_WhenCreatedCourse() {
        assertEquals(0.0f, courseProgress.totalProgress(), "새로 생성된 강좌 학습 진행률은 0%여야 합니다.");
        assertEquals(ProgressStatus.IN_PROGRESS, courseProgress.studyStatus(), "새로 생성된 강좌 상태는 IN_PROGRESS여야 한다.");
    }

    @Test
    @DisplayName("일부 강의만 완료했을 때, 강좌 진행률이 올바르게 계산되어야 한다.")
    void shouldCalculateCoursePercentage_WhenCompletedSomeLecture() {
        courseProgress.updateProgress(lectureProgress1.lectureId());
        courseProgress.updateProgress(lectureProgress2.lectureId());

        assertEquals(66.0f, courseProgress.totalProgress(),"일부 강의가 완료되었을 때, 강좌 진행률이 올바르게 계산되어야 합니다.");
    }

    @Test
    @DisplayName("모든 강의가 100% 완료 시 강좌가 자동으로 완료되어야 한다.")
    void shouldCompleteCourse_WhenWholeLectureCompleted() {
        courseProgress.updateProgress(lectureProgress1.lectureId());
        courseProgress.updateProgress(lectureProgress2.lectureId());
        courseProgress.updateProgress(lectureProgress3.lectureId());

        assertEquals(ProgressStatus.COMPLETED, courseProgress.studyStatus(), "모든 강의가 완료되면 강좌가 완료 상태가 되어야 합니다.");
        assertNotNull(courseProgress.completedAt(), "강좌가 완료되면 완료된 시간이 표시되어야 합니다.");
    }

    @Test
    @DisplayName("완료 상태에서 다시 진도 업데이트를 시도해도 상태가 바뀌지 않아야 한다")
    void shouldNotChangeCourseStatus_WhenUpdateProgressWithStatusIsCompleted() {
        courseProgress.updateProgress(lectureProgress1.lectureId());
        courseProgress.updateProgress(lectureProgress2.lectureId());
        courseProgress.updateProgress(lectureProgress3.lectureId());

        assertThrows(IllegalStateException.class, () -> courseProgress.updateProgress(lectureProgress3.lectureId()));

    }

}