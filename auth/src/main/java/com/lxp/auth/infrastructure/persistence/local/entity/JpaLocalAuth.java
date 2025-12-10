package com.lxp.auth.infrastructure.persistence.local.entity;

import com.lxp.common.infrastructure.persistence.BaseUuidJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "local_auths")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JpaLocalAuth extends BaseUuidJpaEntity implements Persistable<String> {

    @Column(name = "login_identifier")
    private String loginIdentifier;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Transient
    private boolean isNew = true;

    private JpaLocalAuth(UUID id, String loginIdentifier, String hashedPassword) {
        super.setId(id.toString());
        this.loginIdentifier = loginIdentifier;
        this.hashedPassword = hashedPassword;
    }

    public static JpaLocalAuth of(UUID userId, String loginIdentifier, String hashedPassword) {
        return new JpaLocalAuth(userId, loginIdentifier, hashedPassword);
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostLoad
    @PostPersist
    public void markNotNew() {
        this.isNew = false;
    }
}
