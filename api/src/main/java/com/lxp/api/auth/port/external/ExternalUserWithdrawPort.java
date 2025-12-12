package com.lxp.api.auth.port.external;

import com.lxp.api.auth.port.dto.command.UserWithdrawCommand;

public interface ExternalUserWithdrawPort {

    void invalidate(UserWithdrawCommand command);

}
