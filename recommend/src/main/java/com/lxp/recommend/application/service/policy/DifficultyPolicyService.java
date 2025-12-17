package com.lxp.recommend.application.service.policy;

import com.lxp.recommend.domain.dto.DifficultyLevel;
import com.lxp.recommend.domain.dto.LearnerLevel;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 난이도 결정 정책 서비스
 *
 * 책임:
 * - 학습자 레벨 → 추천 강좌 난이도 결정
 *
 * 정책:
 * - JUNIOR: JUNIOR + MIDDLE
 * - MIDDLE: MIDDLE + SENIOR
 * - SENIOR: SENIOR + EXPERT
 * - EXPERT: EXPERT만
 */
@Component
public class DifficultyPolicyService {

    /**
     * 학습자 레벨에 따른 타겟 난이도 결정
     *
     * @param learnerLevel 학습자 레벨 (String)
     * @return 타겟 난이도 목록 (String Set)
     */
    public Set<String> determineTargetDifficulties(String learnerLevel) {
        LearnerLevel level = LearnerLevel.valueOf(learnerLevel);

        Set<DifficultyLevel> difficulties = switch (level) {
            case JUNIOR -> Set.of(DifficultyLevel.JUNIOR, DifficultyLevel.MIDDLE);
            case MIDDLE -> Set.of(DifficultyLevel.MIDDLE, DifficultyLevel.SENIOR);
            case SENIOR -> Set.of(DifficultyLevel.SENIOR, DifficultyLevel.EXPERT);
            case EXPERT -> Set.of(DifficultyLevel.EXPERT);
        };

        return difficulties.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
