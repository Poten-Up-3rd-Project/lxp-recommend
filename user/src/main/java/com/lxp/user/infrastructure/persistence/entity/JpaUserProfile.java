package com.lxp.user.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseJpaEntity;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "user_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JpaUserProfile extends BaseJpaEntity implements Persistable<Long> {

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private JpaUser user;

    @Setter
    @Column(name = "learner_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private LearnerLevel learnerLevel;

    @Setter
    @ElementCollection
    @CollectionTable(
        name = "profile_tags",
        joinColumns = @JoinColumn(name = "id"),
        indexes = @Index(name = "idx_profile_tags_tag_id", columnList = "tag_id")
    )
    @Column(name = "tag_id")
    private List<Long> tags = new ArrayList<>();

    @Setter
    @Column(length = 50)
    private String job;

    @Transient
    private boolean isNew = true;

    @Builder
    public JpaUserProfile(JpaUser user, LearnerLevel learnerLevel, List<Long> tags, String job) {
        this.user = user;
        this.learnerLevel = learnerLevel;
        this.tags = tags;
        this.job = job;
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
