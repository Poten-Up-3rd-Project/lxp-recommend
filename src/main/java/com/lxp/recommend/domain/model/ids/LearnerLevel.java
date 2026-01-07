package com.lxp.recommend.domain.model.ids;

public enum LearnerLevel {
    JUNIOR, MIDDLE, SENIOR, EXPERT;

    /**
     * LearnerLevel → Level (Course 난이도와 매칭용)
     */
    public Level toCommonLevel() {
        return Level.valueOf(this.name());  // ✅ 단순 변환
    }

    /**
     * Level → LearnerLevel
     */
    public static LearnerLevel fromCommonLevel(Level level) {
        return LearnerLevel.valueOf(level.name());
    }
}
