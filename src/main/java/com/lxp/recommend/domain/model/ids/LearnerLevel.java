package com.lxp.recommend.domain.model.ids;

import com.lxp.common.enums.Level;

/**
 * 학습자 레벨 (Recommend BC 전용)
 *
 * Note: Course의 Level과 동일한 값 사용 (common.enums.Level)
 */
public enum LearnerLevel {
    JUNIOR,
    MIDDLE,
    SENIOR,
    EXPERT;

    /**
     * common.enums.Level로 변환
     */
    public Level toCommonLevel() {
        return Level.valueOf(this.name());
    }

    /**
     * common.enums.Level에서 변환
     */
    public static LearnerLevel fromCommonLevel(Level level) {
        return LearnerLevel.valueOf(level.name());
    }
}
