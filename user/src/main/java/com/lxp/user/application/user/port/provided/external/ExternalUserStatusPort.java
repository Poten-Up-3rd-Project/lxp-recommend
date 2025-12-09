package com.lxp.user.application.user.port.provided.external;

@FunctionalInterface
public interface ExternalUserStatusPort {

    String getStatusByUserId(String userId);
}
