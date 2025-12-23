package com.lxp.recommend.infrastructure.external;

import com.lxp.recommend.application.port.required.LearningHistoryQueryPort;
import com.lxp.recommend.application.dto.LearningHistoryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Learning (Enrollment) BC 학습 이력 조회 Adapter
 *
 * 임시 구현: Enrollment BC에 조회 Port가 없어 빈 리스트 반환
 * TODO: api 패키지에 ExternalEnrollmentHistoryPort 추가 필요
 *
 * @see <a href="./ADAPTER_STATUS.md">API 추가 요청 사항</a>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LearningHistoryAdapter implements LearningHistoryQueryPort {

    @Override
    public List<LearningHistoryData> findByLearnerId(String learnerId) {
        // TODO: Enrollment BC에 조회 Port 추가 필요
        log.warn("[Enrollment BC] 조회 Port 미구현. 빈 리스트 반환. learnerId={}", learnerId);
        return Collections.emptyList();
    }
}
