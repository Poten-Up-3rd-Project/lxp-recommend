package com.lxp.user.application.service;

import com.lxp.api.auth.port.dto.result.AuthenticationResponse;
import com.lxp.user.application.port.required.adapter.RegenerateTokenPortHandler;
import com.lxp.user.application.port.required.command.ExecuteUpdateRoleUserCommand;
import com.lxp.user.application.port.required.usecase.UpdateUserRoleUseCase;
import com.lxp.user.domain.common.exception.UserNotFoundException;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserRoleService implements UpdateUserRoleUseCase {

    private final UserRepository userRepository;
    private final RegenerateTokenPortHandler regenerateTokenPortHandler;

    @Override
    public AuthenticationResponse execute(String userId) {
        User user = userRepository.findUserById(UserId.of(userId))
            .orElseThrow(UserNotFoundException::new);
        user.makeInstructor();
        userRepository.save(user);

        return regenerateTokenPortHandler.handle(new ExecuteUpdateRoleUserCommand(userId, user.email(), user.role().name()));
    }
}
