package com.lxp.user.application.port.api.external;

import java.util.Optional;

@FunctionalInterface
public interface ExternalUserStatusPort {

    Optional<String> getStatusByUserId(String userId);
}
