package com.lxp.user.application.service;

import com.lxp.user.application.port.required.command.ExecuteSearchUserCommand;
import com.lxp.user.application.port.required.dto.UserInfoDto;
import com.lxp.user.application.port.required.usecase.SearchUserProfileUseCase;
import com.lxp.user.domain.common.exception.UserNotFoundException;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchUserProfileService implements SearchUserProfileUseCase {

    private final UserRepository userRepository;

    @Override
    public UserInfoDto execute(ExecuteSearchUserCommand command) {
        User user = userRepository.findAggregateUserById(UserId.of(command.userId()))
            .orElseThrow(UserNotFoundException::new);

        UserProfile profile = user.profile();
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
