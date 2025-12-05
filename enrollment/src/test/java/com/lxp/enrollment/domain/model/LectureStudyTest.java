package com.lxp.enrollment.domain.model;

import com.lxp.enrollment.domain.model.vo.LectureStudyId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("강의 단위 학습 진행 테스트")
class LectureStudyTest {

    private LectureStudy lectureStudy;

    @BeforeEach
    void setUp() {
        LectureStudyId lectureStudyId = new LectureStudyId("learnerId_LectureId");

        lectureStudy = LectureStudy.createLectureStudy(lectureStudyId);
    }

    @Test
    @DisplayName("새로 생성된 강의 학습 기록은 완료되지 않은 상태여야 한다")
    void shouldNotCompleted_WhenLectureCreated() {
        assertFalse(lectureStudy.completed(), "새로 생성된 강의 학습 기록은 완료되지 않은 상태여야 합니다.");
    }

    @Test
    @DisplayName("강의 학습 기록을 완료 상태로 변경하면 완료 상태가 true가 되어야 한다")
    void shouldCompletedIsTrue_WhenLectureStatusChangeCompleted() {
        lectureStudy.changeCompleted();
        assertTrue(lectureStudy.completed(), "강의 학습 기록을 완료 상태로 변경하면 완료 상태가 true가 되어야 합니다.");
    }

}