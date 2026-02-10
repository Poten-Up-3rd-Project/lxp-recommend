package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.UserSyncCommand;
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

    @Override
    public void createUser(UserSyncCommand command) {
        if (userRepository.existsById(command.userId())) {
            log.warn("User already exists: {}", command.userId());
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        RecommendUser user = RecommendUser.builder()
                .id(command.userId())
                .interestTags(command.interestTags() != null ? command.interestTags() : new ArrayList<>())
                .level(Level.fromString(command.level()))
                .build();

        userRepository.save(user);
        log.info("Created user: {}", command.userId());
    }

    @Override
    public void updateUser(UserSyncCommand command) {
        RecommendUser user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                command.interestTags() != null ? command.interestTags() : user.getInterestTags(),
                command.level() != null ? Level.fromString(command.level()) : user.getLevel()
        );

        userRepository.save(user);
        log.info("Updated user: {}", command.userId());
    }

    @Override
    public void deleteUser(String userId) {
        RecommendUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.deactivate();
        userRepository.save(user);
        log.info("Deactivated user: {}", userId);
    }
}
