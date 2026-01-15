package com.lxp.recommend.infrastructure.external.member;

import com.lxp.recommend.application.dto.LearnerProfileData;
import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.member.dto.MemberProfileResponse;
import com.lxp.recommend.infrastructure.external.member.dto.TagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Member BC API 어댑터
 * LearnerProfileQueryPort를 구현하여 실제 Member BC API 호출
 */
@Slf4j
@Component
@Profile("!(test | persistence)")  // test 프로파일이 아닐 때만 활성화
@RequiredArgsConstructor
public class MemberApiAdapter implements LearnerProfileQueryPort {

    private final WebClient memberWebClient;

    @Override
    public Optional<LearnerProfileData> getProfile(String learnerId) {
        log.debug("[Member API] Fetching profile for learnerId={}", learnerId);

        try {
            InternalApiResponse<MemberProfileResponse> response = memberWebClient
                    .get()
                    .uri("/internal/api-v1/members/{userId}/profile", learnerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("[Member API] 4xx error for learnerId={}, status={}",
                                learnerId, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Member not found: " + learnerId));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("[Member API] 5xx error for learnerId={}, status={}",
                                learnerId, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Member service error"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<InternalApiResponse<MemberProfileResponse>>() {})
                    .block();  // 동기 호출

            if (response == null || !response.success()) {
                log.warn("[Member API] Failed response for learnerId={}", learnerId);
                return Optional.empty();
            }

            MemberProfileResponse data = response.data();

            // DTO 변환
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
