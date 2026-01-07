package com.lxp.recommend.application.port.required;

import com.lxp.recommend.application.dto.LearnerProfileData;

import java.util.Optional;

/**
 * 학습자 프로필 조회 Port
 *
 * Recommend BC 관점에서 필요한 학습자 정보만 정의
 * (외부 BC의 구조와 무관)
 */
public interface LearnerProfileQueryPort {

    /**
     * 학습자 프로필 조회
     *
     * @param learnerId 학습자 ID
     * @return 학습자 프로필 데이터 (없으면 Optional.empty)
     */
    Optional<LearnerProfileData> getProfile(String learnerId);
}
