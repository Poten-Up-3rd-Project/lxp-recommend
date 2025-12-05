package com.lxp.recommend.domain.port;

import com.lxp.recommend.domain.dto.CourseMetaView;
import com.lxp.recommend.domain.dto.DifficultyLevel;

import java.util.List;
import java.util.Set;

public interface CourseMetaReader {

    /**
     * 1차 필터링: 특정 난이도 목록에 해당하는 강좌들만 조회 (후보군 생성)
     * 예: [JUNIOR, MIDDLE] 또는 [MIDDLE, SENIOR, EXPERT]
     *
     * @param difficulties 조회할 난이도 목록
     * @return 해당 난이도의 강좌 리스트 (메타 정보 포함)
     */
    List<CourseMetaView> findByDifficulties(Set<DifficultyLevel> difficulties);
}
