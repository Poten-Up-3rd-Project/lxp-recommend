package com.lxp.user.application.user.port.provided.external;

import com.lxp.user.application.user.port.provided.dto.UserInfoResponse;

@FunctionalInterface
public interface ExternalUserInfoPort {

    UserInfoResponse userInfo(String userId);

}
