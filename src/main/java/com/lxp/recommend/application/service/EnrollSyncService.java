package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnrollSyncService implements EnrollSyncUseCase {

    private final UserRepository userRepository;
    private final EventIdempotencyUseCase eventIdempotencyUseCase;

    @Override
    public void createEnrollment(EnrollSyncCommand command) {
        if (eventIdempotencyUseCase.isDuplicate(command.eventId())) {
            log.info("Event already processed, skipping: {}", command.eventId());
            return;
        }

        RecommendUser user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.addEnrolledCourse(command.courseId());
        userRepository.save(user);
        log.info("Added enrollment: user={}, course={}", command.userId(), command.courseId());

        eventIdempotencyUseCase.markAsProcessed(command.eventId());
    }

    @Override
    public void deleteEnrollment(EnrollSyncCommand command) {
        if (eventIdempotencyUseCase.isDuplicate(command.eventId())) {
            log.info("Event already processed, skipping: {}", command.eventId());
            return;
        }

        userRepository.findById(command.userId()).ifPresentOrElse(
                user -> {
                    user.removeEnrolledCourse(command.courseId());
                    userRepository.save(user);
                    log.info("Removed enrollment: user={}, course={}", command.userId(), command.courseId());
                },
                () -> log.info("User not found, skipping enrollment delete: user={}, course={}", command.userId(), command.courseId())
        );

        eventIdempotencyUseCase.markAsProcessed(command.eventId());
    }
}
