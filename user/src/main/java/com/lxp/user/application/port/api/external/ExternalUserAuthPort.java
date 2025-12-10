package com.lxp.user.application.port.api.external;

import com.lxp.user.application.port.api.dto.UserAuthResponse;

import java.util.Optional;

public interface ExternalUserAuthPort {

    Optional<UserAuthResponse> userAuth(String userId);

    Optional<UserAuthResponse> getUserInfoByEmail(String email);

}
