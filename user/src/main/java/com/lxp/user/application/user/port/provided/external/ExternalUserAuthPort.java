package com.lxp.user.application.user.port.provided.external;

import com.lxp.user.application.user.port.provided.dto.UserAuthResponse;

public interface ExternalUserAuthPort {

    UserAuthResponse userAuth(String userId);

    UserAuthResponse getUserInfoByEmail(String email);

}
