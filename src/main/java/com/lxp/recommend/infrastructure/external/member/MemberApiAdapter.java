package com.lxp.recommend.infrastructure.external.member;

import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.member.dto.MemberProfileResponse;
import com.lxp.recommend.infrastructure.external.member.dto.TagDto;
import com.lxp.recommend.infrastructure.web.internal.client.MemberServiceFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Member BC API 어댑터 (Feign 기반)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberApiAdapter implements LearnerProfileQueryPort {

    private final MemberServiceFeignClient feignClient;  // ✅ WebClient → FeignClient

    @Override
    public Optional<LearnerProfileData> getProfile(String learnerId) {
        log.debug("[Member API] Fetching profile for learnerId={}", learnerId);

        try {
            InternalApiResponse<MemberProfileResponse> response = feignClient.getMemberProfile(learnerId);

            if (response == null || !response.success() || response.data() == null) {
                log.warn("[Member API] Failed response for learnerId={}", learnerId);
                return Optional.empty();
            }

            MemberProfileResponse data = response.data();

            LearnerProfileData profileData = new LearnerProfileData(
                    data.userId(),
                    data.level(),
                    data.tags().stream()
                            .map(TagDto::content)
                            .collect(Collectors.toSet())
            );

            log.debug("[Member API] Successfully fetched profile: userId={}, level={}, tags={}",
                    data.userId(), data.level(), profileData.interestTags().size());

            return Optional.of(profileData);

        } catch (Exception e) {
            log.error("[Member API] Error fetching profile for learnerId={}", learnerId, e);
            return Optional.empty();
        }
    }
}
