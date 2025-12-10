package com.lxp.user.application.port.api.external;

import com.lxp.user.application.command.UserSaveCommand;

@FunctionalInterface
public interface ExternalUserSavePort {

    void saveUser(UserSaveCommand command);
}
