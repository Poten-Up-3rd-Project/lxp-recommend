package com.lxp.user.application.service;

import com.lxp.user.application.port.required.adapter.UserWithdrawHandler;
import com.lxp.user.application.port.required.command.ExecuteWithdrawUserCommand;
import com.lxp.user.application.port.required.usecase.WithdrawUserUseCase;
import com.lxp.user.domain.common.exception.UserNotFoundException;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawUserService implements WithdrawUserUseCase {

    private final UserRepository userRepository;
    private final UserWithdrawHandler userWithdrawHandler;

    @Override
    @Transactional
    public Void execute(ExecuteWithdrawUserCommand command) {
        User user = userRepository.findUserById(UserId.of(command.userId())).orElseThrow(UserNotFoundException::new);
        user.withdraw();
        userRepository.deactivate(user);

        userWithdrawHandler.handle(command);
        return null;
    }
}
