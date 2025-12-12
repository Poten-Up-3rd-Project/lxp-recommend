package com.lxp.user.application.service;

import com.lxp.api.user.port.dto.command.UserSaveCommand;
import com.lxp.api.user.port.external.ExternalUserSavePort;
import com.lxp.common.enums.Level;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.entity.UserProfile;
import com.lxp.user.domain.profile.model.vo.Tags;
import com.lxp.user.domain.user.exception.UserRoleNotFoundException;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.model.vo.UserEmail;
import com.lxp.user.domain.user.model.vo.UserName;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExternalUserSaveService implements ExternalUserSavePort {

    private final UserRepository userRepository;

    public ExternalUserSaveService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(UserSaveCommand command) {
        userRepository.saveWithProfile(toDomain(command));
    }

    private User toDomain(UserSaveCommand command) {
        log.info(command.toString());
        UserRole role = UserRole.fromString(command.role()).orElseThrow(UserRoleNotFoundException::new);

        final UserId userId = UserId.of(command.userId());
        UserProfile userProfile = null;

        if (role != UserRole.ADMIN) {
            Level level = Level.fromString(command.level()).orElseThrow(UserRoleNotFoundException::new);
            userProfile = UserProfile.create(userId, level, new Tags(command.Tags()), command.job());
        }

        return switch (role) {
            case LEARNER -> User.createLearner(userId, UserName.of(command.name()), UserEmail.of(command.email()), userProfile);
            case INSTRUCTOR ->
                User.createInstructor(userId, UserName.of(command.name()), UserEmail.of(command.email()), userProfile);
            case ADMIN ->
                User.createAdmin(userId, UserName.of(command.name()), UserEmail.of(command.email()));
        };
    }
}
