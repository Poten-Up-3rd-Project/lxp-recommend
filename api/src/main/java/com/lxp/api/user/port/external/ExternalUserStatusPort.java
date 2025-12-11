package com.lxp.api.user.port.external;

import java.util.Optional;

@FunctionalInterface
public interface ExternalUserStatusPort {

    Optional<String> getStatusByUserId(String userId);
}
