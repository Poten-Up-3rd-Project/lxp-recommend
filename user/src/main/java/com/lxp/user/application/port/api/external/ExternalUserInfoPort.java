package com.lxp.user.application.port.api.external;

import com.lxp.user.application.port.api.dto.UserInfoResponse;

import java.util.Optional;

@FunctionalInterface
public interface ExternalUserInfoPort {

    Optional<UserInfoResponse> userInfo(String userId);

}
