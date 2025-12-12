package com.lxp.user.infrastructure.persistence.adapter;

import com.lxp.user.domain.common.exception.UserNotFoundException;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.exception.ProfileNotFoundException;
import com.lxp.user.domain.user.model.entity.User;
import com.lxp.user.domain.user.model.vo.UserStatus;
import com.lxp.user.domain.user.repository.UserRepository;
import com.lxp.user.infrastructure.persistence.entity.JpaUser;
import com.lxp.user.infrastructure.persistence.entity.JpaUserProfile;
import com.lxp.user.infrastructure.persistence.repository.JpaUserProfileRepository;
import com.lxp.user.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserDomainMapper userDomainMapper;
    private final JpaUserProfileRepository jpaUserProfileRepository;

    @Override
    public Optional<UserStatus> findUserStatusById(UserId userId) {
        return jpaUserRepository.findUserStatusById(userId.asString());
    }

    @Override
    public Optional<User> findUserById(UserId userId) {
        return jpaUserRepository.findById(userId.asString()).map(userDomainMapper::toDomain);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userDomainMapper::toDomain);
    }

    @Override
    public Optional<User> findAggregateUserById(UserId userId) {
        JpaUserProfile jpaUserProfile = jpaUserProfileRepository.findByUserId(userId.asString()).orElse(null);
        return jpaUserRepository.findById(userId.asString())
            .map(jpaUser -> userDomainMapper.toDomain(jpaUser, jpaUserProfile));
    }

    @Override
    public Optional<User> findAggregateUserByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
            .map(jpaUser -> userDomainMapper.toDomain(
                jpaUser,
                jpaUserProfileRepository.findByUser(jpaUser).orElse(null)
            ));
    }

    @Override
    public void save(User user) {
        if (jpaUserRepository.existsById(user.id().asString())) {
            JpaUser jpaUser = jpaUserRepository.findById(user.id().asString())
                .orElseThrow(UserNotFoundException::new);

            userDomainMapper.updateUserFromDomain(user, jpaUser);
            return;
        }
        jpaUserRepository.save(userDomainMapper.toEntity(user));
    }

    @Override
    public void saveWithProfile(User user) {

        if (jpaUserRepository.existsById(user.id().asString())) {
            JpaUser jpaUser = jpaUserRepository.findById(user.id().asString())
                .orElseThrow(UserNotFoundException::new);

            JpaUserProfile existingJpaUserProfile = jpaUserProfileRepository.findByUser(jpaUser)
                .orElseThrow(ProfileNotFoundException::new);

            userDomainMapper.updateUserFromDomain(user, jpaUser);
            userDomainMapper.updateProfileEntityFromDomain(user.profile(), existingJpaUserProfile);
            return;
        }

        JpaUser entity = userDomainMapper.toEntity(user);
        JpaUser jpaUser = jpaUserRepository.save(entity);

        JpaUserProfile jpaUserProfile = userDomainMapper.toEntity(user.profile());
        jpaUserProfile.setUser(jpaUser);
        jpaUserProfileRepository.save(jpaUserProfile);
    }

    @Override
    public void deactivate(User user) {
        JpaUser jpaUser = jpaUserRepository.findById(user.id().asString()).orElseThrow(UserNotFoundException::new);
        userDomainMapper.updateUserFromDomain(user, jpaUser);
    }

}
