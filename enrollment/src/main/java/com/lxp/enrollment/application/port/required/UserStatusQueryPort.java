package com.lxp.enrollment.application.port.required;

public interface UserStatusQueryPort {
    boolean isActiveUser(String userId);
}
