package com.lxp.recommend.infrastructure.external.member.dto;

import java.util.List;

/**
 * Member BC로부터 받는 프로필 응답
 */
public record MemberProfileResponse(
        String userId,
        String learnerLevel,
        List<Long> interestTagIds
        ) {
}
/** UserBC
 * public record UserProfileInternalResult(
 *     String userId,
 *     List<Long> interestTagIds,
 *     String learnerLevel
 */