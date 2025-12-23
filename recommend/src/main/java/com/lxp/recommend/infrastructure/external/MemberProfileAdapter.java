package com.lxp.recommend.infrastructure.external;

import com.lxp.api.tag.port.external.TagCachePort;
import com.lxp.api.user.port.dto.result.UserInfoResponse;
import com.lxp.api.user.port.external.ExternalUserInfoPort;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.dto.LearnerProfileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Member BC 프로필 조회 Adapter (Anti-Corruption Layer)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MemberProfileAdapter implements LearnerProfileQueryPort {

    private final ExternalUserInfoPort externalUserInfoPort;
    private final TagCachePort tagCachePort;

    @Override
    public Optional<LearnerProfileData> getProfile(String learnerId) {
        try {
            return externalUserInfoPort.userInfo(learnerId)
                    .map(this::toInternalData);
        } catch (Exception e) {
            log.error("[Member BC 호출 실패] learnerId={}, error={}", learnerId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private LearnerProfileData toInternalData(UserInfoResponse response) {
        return new LearnerProfileData(
                response.userId(),
                extractLearnerLevel(response),
                extractInterestTags(response)
        );
    }

    private String extractLearnerLevel(UserInfoResponse response) {
        if (response.profile() == null || response.profile().level() == null) {
            log.warn("[Member BC] level 정보 없음. JUNIOR로 기본 설정. userId={}", response.userId());
            return "JUNIOR";
        }
        return response.profile().level();
    }

    private Set<String> extractInterestTags(UserInfoResponse response) {
        if (response.profile() == null || response.profile().tags() == null) {
            log.warn("[Member BC] tags 정보 없음. 빈 Set 반환. userId={}", response.userId());
            return Set.of();
        }

        try {
            var tagIds = new HashSet<>(response.profile().tags());
            return tagCachePort.findByIds(tagIds).stream()
                    .filter(tagResult -> "ACTIVE".equals(tagResult.state()))
                    .map(tagResult -> tagResult.name())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("[Tag 조회 실패] userId={}, error={}", response.userId(), e.getMessage(), e);
            return Set.of();
        }
    }
}
