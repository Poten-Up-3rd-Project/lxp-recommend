package com.lxp.user.application.port.required.command;

import com.lxp.common.application.cqrs.Command;

public record ExecuteWithdrawUserCommand(
    String userId,
    String cookie
) implements Command {
}
