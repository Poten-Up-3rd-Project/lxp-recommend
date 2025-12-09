package com.lxp.auth.presentation.rest.local.dto.reqeust;

import com.lxp.auth.application.local.port.required.command.HandleLoginCommand;

public record LoginRequest(String email, String password) {

    public HandleLoginCommand toCommand() {
        return new HandleLoginCommand(this.email, this.password);
    }
}
