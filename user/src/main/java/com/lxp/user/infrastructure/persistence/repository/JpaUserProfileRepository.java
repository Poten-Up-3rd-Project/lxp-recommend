package com.lxp.user.infrastructure.persistence.repository;

import com.lxp.user.infrastructure.persistence.entity.JpaUserProfile;
import com.lxp.user.infrastructure.persistence.entity.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserProfileRepository extends JpaRepository<JpaUserProfile, Long> {

    Optional<JpaUserProfile> findByUserId(String userId);

    Optional<JpaUserProfile> findByUser(JpaUser user);

}
