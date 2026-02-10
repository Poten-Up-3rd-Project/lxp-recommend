package com.lxp.recommend.application.service;

import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.port.in.EnrollSyncUseCase;
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

    @Override
    public void createEnrollment(EnrollSyncCommand command) {
        RecommendUser user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.addEnrolledCourse(command.courseId());
        userRepository.save(user);
        log.info("Added enrollment: user={}, course={}", command.userId(), command.courseId());
    }

    @Override
    public void deleteEnrollment(EnrollSyncCommand command) {
        RecommendUser user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.removeEnrolledCourse(command.courseId());
        userRepository.save(user);
        log.info("Removed enrollment: user={}, course={}", command.userId(), command.courseId());
    }
}
