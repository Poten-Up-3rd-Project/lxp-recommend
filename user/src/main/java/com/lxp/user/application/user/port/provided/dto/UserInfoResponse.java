package com.lxp.user.application.user.port.provided.dto;

import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.user.model.entity.User;

import java.util.List;

public record UserInfoResponse(
    String userId,
    String name,
    String email,
    String role,
    ProfileDto profile
) {
    public static UserInfoResponse of(User user, UserProfile profile) {
        return new UserInfoResponse(
            user.id().asString(),
            user.name(),
            user.email(),
            user.role().name(),
            ProfileDto.of(profile)
        );
    }

    public record ProfileDto(
        String profileId,
        List<Long> tags,
        String learnerLevel,
        String job
    ) {
        public static ProfileDto of(UserProfile profile) {
            return new ProfileDto(
                profile.id().asString(),
                profile.tags().values(),
                profile.level().name(),
                profile.job()
            );
        }
    }
}
