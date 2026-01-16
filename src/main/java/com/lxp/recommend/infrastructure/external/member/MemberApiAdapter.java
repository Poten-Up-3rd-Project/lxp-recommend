package com.lxp.recommend.infrastructure.external.member;

import com.lxp.common.infrastructure.exception.ErrorResponse;
import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.member.dto.MemberProfileResponse;
import com.lxp.recommend.infrastructure.external.member.dto.TagDto;
import com.lxp.recommend.infrastructure.web.internal.client.MemberServiceFeignClient;
import feign.FeignException;
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

    private final MemberServiceFeignClient feignClient;

    @Override
    public Optional<LearnerProfileData> getProfile(String learnerId) {
        log.debug("[Member API] Fetching profile for learnerId={}", learnerId);

        try {
            InternalApiResponse<MemberProfileResponse> response = feignClient.getMemberProfile(learnerId);

            if (response == null) {
                log.warn("[Member API] Null response for learnerId={}", learnerId);
                return Optional.empty();
            }

            if (!response.success()) {
                ErrorResponse error = response.error();
                log.warn("[Member API] Error response: code={}, message={}, group={}",
                        error.getCode(), error.getMessage(), error.getGroup());
                return Optional.empty();
            }

            if (response.data() == null) {
                log.warn("[Member API] Null data for learnerId={}", learnerId);
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

        } catch (FeignException e) {
            log.error("[Member API] Feign error for learnerId={}: status={}, message={}",
                    learnerId, e.status(), e.getMessage());
            return Optional.empty();

        } catch (Exception e) {
            log.error("[Member API] Unexpected error fetching profile for learnerId={}", learnerId, e);
            return Optional.empty();
        }
    }
}