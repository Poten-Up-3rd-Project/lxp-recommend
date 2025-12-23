package com.lxp.recommend.application.port.required;

import com.lxp.recommend.application.dto.LearningHistoryData;

import java.util.List;

/**
 * 학습 이력 조회 Port
 *
 * 학습자의 수강 이력 조회
 */
public interface LearningHistoryQueryPort {

    /**
     * 학습자의 전체 학습 이력 조회
     *
     * @param learnerId 학습자 ID
     * @return 학습 이력 리스트
     */
    List<LearningHistoryData> findByLearnerId(String learnerId);
}
