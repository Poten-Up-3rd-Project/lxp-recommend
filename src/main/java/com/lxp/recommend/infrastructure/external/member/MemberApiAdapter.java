package com.lxp.recommend.infrastructure.external.member;

import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.member.dto.MemberProfileResponse;
import com.lxp.recommend.infrastructure.web.internal.client.MemberServiceFeignClient;
import com.lxp.common.infrastructure.exception.ErrorResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Member BC API 어댑터 (Feign 기반)
 *
 * Anti-Corruption Layer 역할:
 * - User BC의 외부 모델을 Recommend BC의 내부 모델로 변환
 * - 용어 매핑: User BC "userId" = Recommend BC "learnerId"
 * - 타입 변환: List<Long> interestTagIds → Set<String> interestTags
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
            // User BC는 이 파라미터를 userId로 인식
            InternalApiResponse<MemberProfileResponse> response =
                    feignClient.getMemberProfile(learnerId);

            // 응답 검증
            if (response == null) {
                log.warn("[Member API] Null response for learnerId={}", learnerId);
                return Optional.empty();
            }

            if (!response.success()) {
                ErrorResponse error = (ErrorResponse) response.error();
                log.warn("[Member API] Error response for learnerId={}: code={}, message={}, group={}",
                        learnerId, error.getCode(), error.getMessage(), error.getGroup());
                return Optional.empty();
            }

            if (response.data() == null) {
                log.warn("[Member API] Null data for learnerId={}", learnerId);
                return Optional.empty();
            }

            MemberProfileResponse data = response.data();

            // Tag ID → Tag Content 변환 (임시 구현)
            // TODO: 나중에 Tag 서비스 연동하여 실제 태그명 조회
            Set<String> tagContents = data.interestTagIds().stream()
                    .map(tagId -> "tag-" + tagId)  // 임시: "tag-1", "tag-2"
                    .collect(Collectors.toSet());

            // 외부 모델 → 내부 모델 변환
            LearnerProfileData profileData = new LearnerProfileData(
                    data.userId(),        // User BC의 userId → Recommend BC의 learnerId (의미 동일)
                    data.learnerLevel(),
                    tagContents
            );

            log.debug("[Member API] Successfully fetched profile: userId={}, learnerLevel={}, tagIds={}",
                    data.userId(), data.learnerLevel(), data.interestTagIds());

            return Optional.of(profileData);

        } catch (FeignException.NotFound e) {
            log.warn("[Member API] Member not found: learnerId={}", learnerId);
            return Optional.empty();

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
