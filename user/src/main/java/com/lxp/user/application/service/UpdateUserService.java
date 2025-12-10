package com.lxp.user.application.service;

import com.lxp.user.application.port.required.command.ExecuteUpdateUserCommand;
import com.lxp.user.application.port.required.dto.UserInfoDto;
import com.lxp.user.application.port.required.usecase.UpdateUserProfileUseCase;
import com.lxp.user.domain.common.exception.UserInactiveException;
import com.lxp.user.domain.common.exception.UserNotFoundException;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserProfileUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = UserNotFoundException.class)
    public UserInfoDto execute(ExecuteUpdateUserCommand command) {
        User user = userRepository.findAggregateUserById(UserId.of(command.userId()))
            .orElseThrow(UserNotFoundException::new);
        UserProfile profile = user.profile();

        if (!user.isActive()) {
            throw new UserInactiveException("비활성화된 사용자는 정보를 업데이트할 수 없습니다.");
        }

        UserName userName = command.name() == null ? null : UserName.of(command.name());
        LearnerLevel learnerLevel = LearnerLevel.fromString(command.level()).orElseGet(null);

        user.update(userName, learnerLevel, command.tags(), command.job());

        userRepository.save(user);

        return new UserInfoDto(
            command.userId(),
            user.name(),
            user.email(),
            profile.tags().values(),
            profile.level().name(),
            profile.job()
        );
    }
}
