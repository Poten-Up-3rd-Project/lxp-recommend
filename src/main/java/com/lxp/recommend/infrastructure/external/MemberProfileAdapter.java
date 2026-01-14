package com.lxp.recommend.infrastructure.external;


import com.lxp.recommend.application.port.required.LearnerProfileQueryPort;
import com.lxp.recommend.application.dto.LearnerProfileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("test")
public class MemberProfileAdapter implements LearnerProfileQueryPort {


    @Override
    public Optional<LearnerProfileData> getProfile(String learnerId) {
        log.info("[User BC] Mock 실행. learnerId={}", learnerId);

        // Mock 데이터 반환
        LearnerProfileData mockProfile = new LearnerProfileData(
                learnerId,
                "JUNIOR",  // 기본 레벨
                Set.of("Java", "Spring", "Database")  // 기본 관심 태그
        );
        return Optional.of(mockProfile);

        /* TODO: api 모듈 배포 후 주석 해제
        try {
            return externalUserInfoPort.getUserInfo(learnerId)
                    .map(this::toInternalData);
        } catch (Exception e) {
            log.error("[User BC 호출 실패] learnerId={}, error={}", learnerId, e.getMessage(), e);
            return Optional.empty();
        }
        */
    }

    /* 주석 처리
    private LearnerProfileData toInternalData(UserInfoResponse response) { ... }
    private String extractLearnerLevel(UserInfoResponse response) { ... }
    private Set<String> extractInterestTags(UserInfoResponse response) { ... }
    */
}
