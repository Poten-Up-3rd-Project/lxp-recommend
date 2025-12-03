package com.lxp.recommend.domain.dto;

import java.util.Set;

public record LearnerProfileView(
        Long memberId,          // UUID -> Long
        Set<String> interestTags,
        Set<String> skills,
        DifficultyLevel level   // Enum (별도 파일 필요)
) {}
