package com.lxp.user.application.port.required.usecase;

import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.user.application.port.required.command.ExecuteWithdrawUserCommand;

@FunctionalInterface
public interface WithdrawUserUseCase extends CommandWithResultUseCase<ExecuteWithdrawUserCommand, Void> {
}
