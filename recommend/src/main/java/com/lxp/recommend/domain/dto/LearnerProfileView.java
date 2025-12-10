package com.lxp.recommend.domain.dto;

import java.util.Set;

public record LearnerProfileView(
        String memberId,
        Set<String> selectedTags,
        LearnerLevel learnerLevel
) {}
