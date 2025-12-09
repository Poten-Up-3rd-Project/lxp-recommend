package com.lxp.recommend.domain.port;

import com.lxp.recommend.domain.dto.LearningStatusView;

import java.util.List;

public interface LearningStatusReader {
    /**
     * 특정 회원의 수강 이력 조회
     * @param memberId 회원 ID
     * @return 수강 상태 리스트 (수강중, 완료 등)
     */
    List<LearningStatusView> findByMemberId(String memberId);
}
