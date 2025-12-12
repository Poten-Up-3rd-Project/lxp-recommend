package com.lxp.user.infrastructure.persistence.adapter;

import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.profile.model.vo.Tags;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.infrastructure.persistence.entity.JpaUser;
import com.lxp.user.infrastructure.persistence.entity.JpaUserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDomainMapper {

    public JpaUser toEntity(User user) {
        return JpaUser.of(
            user.getId().value(),
            user.name(),
            user.email(),
            user.role(),
            user.userStatus(),
            user.deletedAt()
        );
    }

    public User toDomain(JpaUser jpaUser) {
        return User.of(
            UserId.of(jpaUser.getId()),
            UserName.of(jpaUser.getName()),
            UserEmail.of(jpaUser.getEmail()),
            jpaUser.getRole(),
            jpaUser.getUserStatus(),
            null,
            jpaUser.getDeletedAt()
        );
    }

    public User toDomain(JpaUser jpaUser, JpaUserProfile jpaProfile) {
        UserId userId = UserId.of(jpaUser.getId());
        UserProfile userProfile = toDomain(jpaProfile, userId);

        return User.of(
            userId,
            UserName.of(jpaUser.getName()),
            UserEmail.of(jpaUser.getEmail()),
            jpaUser.getRole(),
            jpaUser.getUserStatus(),
            userProfile,
            jpaUser.getDeletedAt()
        );
    }

    public JpaUserProfile toEntity(UserProfile profile) {
        return JpaUserProfile.builder()
            .level(profile.level())
            .tags(profile.tags().values())
            .job(profile.job())
            .build();
    }

    public UserProfile toDomain(JpaUserProfile jpaProfile, UserId userId) {
        return UserProfile.create(userId, jpaProfile.getLevel(), new Tags(jpaProfile.getTags()), jpaProfile.getJob());
    }

    public void updateUserFromDomain(User user, JpaUser jpaUser) {
        jpaUser.update(user.name(), user.role(), user.userStatus(), user.deletedAt());
    }

    public void updateProfileEntityFromDomain(UserProfile profile, JpaUserProfile existingEntity) {
        existingEntity.setLevel(profile.level());
        existingEntity.setJob(profile.job());

        List<Long> existingTags = existingEntity.getTags();
        existingTags.clear();
        existingTags.addAll(profile.tags().values());
    }
}
