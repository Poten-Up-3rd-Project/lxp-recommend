package com.lxp.auth.presentation.rest.local.dto.reqeust;

import com.lxp.auth.application.local.port.required.command.HandleUserRegisterCommand;

import java.util.List;

public record RegisterRequest(
    String email,
    String password,
    String name,
    String role,
    List<Long> tags,
    String learnerLevel,
    String job
) {

    public HandleUserRegisterCommand toCommand() {
        return new HandleUserRegisterCommand(
            this.email,
            this.password,
            this.name,
            this.role,
            this.tags,
            this.learnerLevel,
            this.job
        );
    }
}
