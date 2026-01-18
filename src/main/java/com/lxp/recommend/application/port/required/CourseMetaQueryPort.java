package com.lxp.recommend.application.port.required;

import com.lxp.recommend.application.dto.CourseMetaData;
import com.lxp.recommend.infrastructure.external.course.dto.CourseMetaResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 강좌 메타 정보 조회 Port
 *
 * 추천 계산에 필요한 강좌 정보만 조회
 * (섹션/강의 구조는 불필요)
 */
public interface CourseMetaQueryPort {

    /**
     * 난이도별 강좌 조회
     *
     * @param targetDifficulties 타겟 난이도 목록 (예: JUNIOR, MIDDLE)
     * @param limit 최대 조회 개수
     * @return 강좌 메타 데이터 리스트
     */
    List<CourseMetaData> findByDifficulties(Set<String> targetDifficulties, int limit);

    List<CourseMetaResponse> findByCourses(List<String> courseIds);
}
