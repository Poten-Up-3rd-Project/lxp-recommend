package com.lxp.recommend.domain.port;

import com.lxp.recommend.domain.dto.CourseMetaView;
import com.lxp.recommend.domain.dto.DifficultyLevel;

import java.util.List;
import java.util.Set;

public interface CourseMetaReader {
    /**
     * 추천 후보 강좌 조회 (1차 필터링)
     * @param tags 관심 태그 목록
     * @param level 사용자 레벨
     * @param limit 최대 조회 개수 (성능 제어용)
     * @return 필터링된 강좌 메타 정보 리스트
     */
    List<CourseMetaView> findCandidates(Set<String> tags, DifficultyLevel level, int limit);
}
