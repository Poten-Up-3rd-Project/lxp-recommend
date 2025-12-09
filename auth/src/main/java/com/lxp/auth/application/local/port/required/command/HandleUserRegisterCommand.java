package com.lxp.auth.application.local.port.required.command;

import com.lxp.common.application.cqrs.Command;

import java.util.List;

public record HandleUserRegisterCommand(
    String email,
    String password,
    String name,
    String role,
    List<Long> tags,
    String learnerLevel,
    String job
) implements Command {
}
