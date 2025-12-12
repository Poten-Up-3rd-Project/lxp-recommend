package com.lxp.user.infrastructure.persistence.repository;

import com.lxp.user.domain.user.model.vo.UserStatus;
import com.lxp.user.infrastructure.persistence.entity.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<JpaUser, String> {

    boolean existsById(String id);

    Optional<UserStatus> findUserStatusById(String userId);

    Optional<JpaUser> findByEmail(String email);

    @Query("SELECT u FROM JpaUser u LEFT JOIN JpaUserProfile p ON p.user = u WHERE u.email = :email")
    Optional<JpaUser> findByEmailWithProfile(@Param("email") String email);
}
