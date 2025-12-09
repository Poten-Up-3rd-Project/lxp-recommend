package com.lxp.recommend.infrastructure.messaging;

import com.lxp.common.domain.event.DomainEvent;
// 타 BC의 이벤트 클래스 import (EnrollmentCreatedEvent 등)
// (실제로는 common-lib이나 타 모듈의 event 패키지를 참조해야 함)
//import com.lxp.enrollment.domain.event.EnrollmentCreatedEvent;
import com.lxp.recommend.application.RecommendationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventListener {

    private final RecommendationApplicationService recommendationService;

    /**
     * 수강 신청 발생 시 -> 추천 재계산 트리거
     * (Spring 내부 이벤트 @EventListener 사용 가정)
     */

    /**
     *     @Async // 리스너 자체도 비동기로 돌리면 더 좋음
     *     @EventListener
     *     public void handleEnrollmentCreated(EnrollmentCreatedEvent event) {
     *         log.info("[이벤트 수신] 수강신청 발생: {}", event);
     *
     *         // event.getMemberId()가 String(UUID)라고 가정
     *         // EnrollmentCreatedEvent의 구조에 따라 파싱 필요할 수 있음
     *         String memberId = event.getUserId().toString();
     *
     *         recommendationService.refreshRecommendationAsync(memberId);
     *     }
     *
     * @param event
     */


    // MemberUpdatedEvent, CourseUpdatedEvent 등도 유사하게 처리
}
