package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.in.UserSyncUseCase;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserSyncService implements UserSyncUseCase {

    private final UserRepository userRepository;
    private final EventIdempotencyUseCase eventIdempotencyUseCase;

    @Override
    public void createUser(UserSyncCommand command) {
        if (eventIdempotencyUseCase.isDuplicate(command.eventId())) {
            log.info("Event already processed, skipping: {}", command.eventId());
            return;
        }

        if (userRepository.existsById(command.userId())) {
            log.info("User already exists, skipping: {}", command.userId());
            return;
        }

        RecommendUser user = RecommendUser.builder()
                .id(command.userId())
                .interestTags(command.interestTags() != null ? command.interestTags() : new ArrayList<>())
                .level(Level.fromString(command.level()))
                .build();

        userRepository.save(user);
        log.info("Created user: {}", command.userId());

        eventIdempotencyUseCase.markAsProcessed(command.eventId());
    }

    @Override
    public void updateUser(UserSyncCommand command) {
        if (eventIdempotencyUseCase.isDuplicate(command.eventId())) {
            log.info("Event already processed, skipping: {}", command.eventId());
            return;
        }

        RecommendUser user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                command.interestTags() != null ? command.interestTags() : user.getInterestTags(),
                command.level() != null ? Level.fromString(command.level()) : user.getLevel()
        );

        userRepository.save(user);
        log.info("Updated user: {}", command.userId());

        eventIdempotencyUseCase.markAsProcessed(command.eventId());
    }

    @Override
    public void deleteUser(String eventId, String userId) {
        if (eventIdempotencyUseCase.isDuplicate(eventId)) {
            log.info("Event already processed, skipping: {}", eventId);
            return;
        }

        userRepository.findById(userId).ifPresentOrElse(
                user -> {
                    user.deactivate();
                    userRepository.save(user);
                    log.info("Deactivated user: {}", userId);
                },
                () -> log.info("User not found, skipping delete: {}", userId)
        );

        eventIdempotencyUseCase.markAsProcessed(eventId);
    }
}
