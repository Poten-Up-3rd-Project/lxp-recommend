package com.lxp.recommend.infrastructure.external.common;

import com.lxp.recommend.domain.model.ids.Level;

import java.util.Set;

/**
 * Recommend BC의 Level 매핑 유틸리티
 *
 * 목적:
 * - Course BC의 common.enums.Level 사용
 * - 추천 로직에서 Level 변환 제공
 *
 * MSA 전환 시:
 * - 이 클래스가 ACL(Anti-Corruption Layer) 역할
 * - Course BC의 Level이 변경되어도 여기만 수정
 */
public class LevelMapper {

    /**
     * 학습자 레벨에 따른 추천 강좌 난이도 결정
     *
     * 정책:
     * - JUNIOR: JUNIOR + MIDDLE
     * - MIDDLE: MIDDLE + SENIOR
     * - SENIOR: SENIOR + EXPERT
     * - EXPERT: EXPERT만
     */
    public static Set<Level> determineTargetLevels(Level learnerLevel) {
        return switch (learnerLevel) {
            case JUNIOR -> Set.of(Level.JUNIOR, Level.MIDDLE);
            case MIDDLE -> Set.of(Level.MIDDLE, Level.SENIOR);
            case SENIOR -> Set.of(Level.SENIOR, Level.EXPERT);
            case EXPERT -> Set.of(Level.SENIOR, Level.EXPERT);
        };
    }

    /**
     * Level → String 변환 (Port 호출용)
     */
    public static Set<String> toStringSet(Set<Level> levels) {
        return levels.stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet());
    }
}
