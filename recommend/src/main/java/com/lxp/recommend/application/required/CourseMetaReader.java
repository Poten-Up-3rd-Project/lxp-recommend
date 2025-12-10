package com.lxp.recommend.application.required;

import com.lxp.recommend.domain.dto.CourseMetaView;
import com.lxp.recommend.domain.dto.DifficultyLevel;

import java.util.List;
import java.util.Set;

public interface CourseMetaReader {

    /**
     * 1차 필터링: 특정 난이도 목록에 해당하는 강좌들을 최신순으로 조회
     */
    List<CourseMetaView> findByDifficulties(Set<DifficultyLevel> difficulties, int limit);

    /**
     * 특정 ID 목록에 해당하는 강좌 정보 조회 (수강 중인 강좌 태그 수집용) - 이후 유지보수
     */
    List<CourseMetaView> findAllByIds(Set<String> courseIds);
}
