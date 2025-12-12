package com.lxp.user.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseUuidJpaEntity;
import com.lxp.user.domain.user.model.vo.UserRole;
import com.lxp.user.domain.user.model.vo.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JpaUser extends BaseUuidJpaEntity
//    implements Persistable<String>
{

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

//    @Transient
//    private boolean isNew = true;

    @Builder
    public JpaUser(String id, String name, String email, UserRole role, UserStatus userStatus, LocalDateTime deletedAt) {
        super.setId(id);
        this.name = name;
        this.email = email;
        this.role = role;
        this.userStatus = userStatus;
        this.deletedAt = deletedAt;
    }

    public static JpaUser of(UUID id, String name, String email, UserRole role, UserStatus userStatus, LocalDateTime deletedAt) {
        return new JpaUser(id.toString(), name, email, role, userStatus, deletedAt);
    }

    public void update(String name, UserRole role, UserStatus userStatus, LocalDateTime deletedAt) {
        this.name = name;
        this.role = role;
        this.userStatus = userStatus;
        this.deletedAt = deletedAt;
    }

//
//    @Override
//    public boolean isNew() {
//        return this.isNew;
//    }
//
//    @PostLoad
//    @PostPersist
//    public void markNotNew() {
//        this.isNew = false;
//    }
}
