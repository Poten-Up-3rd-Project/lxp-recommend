package com.lxp.enrollment.infrastructure.external.adapter;

import com.lxp.api.user.port.external.ExternalUserStatusPort;
import com.lxp.enrollment.application.port.required.UserStatusQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserStatusQueryAdapter implements UserStatusQueryPort {
    private final ExternalUserStatusPort externalUserStatusPort;

    @Override
    public boolean isActiveUser(String userId) {
        String status = externalUserStatusPort.getStatusByUserId(userId).orElse(null);
        if (status == null) {
            return false;
        }

        return status.toUpperCase(Locale.ROOT).equals("ACTIVE");
    }
}
