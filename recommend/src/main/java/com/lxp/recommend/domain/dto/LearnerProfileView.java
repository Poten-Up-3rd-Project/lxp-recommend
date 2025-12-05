package com.lxp.recommend.domain.dto;

import java.util.Set;

public record LearnerProfileView(
        Long memberId,
        Set<String> selectedTags,
        CareerType career // 추가됨
        // DifficultyLevel level; <- 이건 이제 불필요하므로 삭제하거나 career로 대체
) {}
