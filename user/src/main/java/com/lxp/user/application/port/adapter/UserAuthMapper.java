package com.lxp.user.application.port.adapter;

import com.lxp.api.user.port.dto.result.UserAuthResponse;
import com.lxp.api.user.port.dto.result.UserInfoResponse;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserAuthMapper {

    public UserAuthResponse toResponse(User user) {
        return new UserAuthResponse(
            user.id().asString(),
            user.email(),
            user.role().name()
        );
    }

    public UserInfoResponse toInfoResponse(User user) {
        UserProfile profile = user.profile();
        return new UserInfoResponse(
            user.id().asString(),
            user.name(),
            user.email(),
            user.role().name(),
            user.userStatus().name(),
            new UserInfoResponse.ProfileDto(
                profile.tags().values(),
                profile.level().name(),
                profile.job()
            )
        );
    }
}
