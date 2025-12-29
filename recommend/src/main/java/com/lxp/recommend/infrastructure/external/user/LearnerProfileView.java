package com.lxp.recommend.infrastructure.external.user;

import com.lxp.recommend.domain.model.ids.LearnerLevel;

import java.util.Set;

public record LearnerProfileView(
        String memberId,
        Set<String> selectedTags,
        LearnerLevel learnerLevel
) {}
