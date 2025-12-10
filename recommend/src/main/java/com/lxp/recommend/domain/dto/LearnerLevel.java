package com.lxp.recommend.domain.dto;

/**
 * 학습자 레벨 (사용자의 현재 수준)
 */
public enum LearnerLevel {
    JUNIOR,
    MIDDLE,
    SENIOR,
    EXPERT;

    /**
     * 현재 레벨과 한 단계 위 레벨을 반환
     * @return 추천 대상 난이도 목록 (최대 2개)
     */
    public DifficultyLevel[] getRecommendedDifficulties() {
        return switch (this) {
            case JUNIOR -> new DifficultyLevel[]{DifficultyLevel.JUNIOR, DifficultyLevel.MIDDLE};
            case MIDDLE -> new DifficultyLevel[]{DifficultyLevel.MIDDLE, DifficultyLevel.SENIOR};
            case SENIOR -> new DifficultyLevel[]{DifficultyLevel.SENIOR, DifficultyLevel.EXPERT};
            case EXPERT -> new DifficultyLevel[]{DifficultyLevel.EXPERT}; // 최고 레벨, 자기 레벨만
        };
    }
}
