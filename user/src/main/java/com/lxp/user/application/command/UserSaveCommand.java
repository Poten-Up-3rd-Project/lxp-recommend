package com.lxp.user.application.command;

import com.lxp.common.application.cqrs.Command;

import java.util.List;
import java.util.UUID;

public record UserSaveCommand(
    UUID userId,
    String name,
    String email,
    String role,

    List<Long> Tags,
    String learnerLevel,
    String job
) implements Command {
}
