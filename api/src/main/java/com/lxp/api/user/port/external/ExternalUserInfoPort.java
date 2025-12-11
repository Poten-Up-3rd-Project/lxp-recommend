package com.lxp.api.user.port.external;

import com.lxp.api.user.port.dto.result.UserInfoResponse;

import java.util.Optional;

@FunctionalInterface
public interface ExternalUserInfoPort {

    Optional<UserInfoResponse> userInfo(String userId);

}
