package com.lxp.user.application.user.port.provided.external;

import com.lxp.user.application.user.command.UserSaveCommand;

@FunctionalInterface
public interface ExternalUserSavePort {

    void saveUser(UserSaveCommand command);
}
