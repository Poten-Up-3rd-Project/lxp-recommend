package com.lxp.user.domain.profile.model.entity;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import com.lxp.user.domain.profile.model.vo.Tags;
import com.lxp.user.domain.profile.model.vo.UserProfileId;

import java.util.List;
import java.util.Objects;

public class UserProfile extends AggregateRoot<UserProfileId> {

    private UserProfileId id;
    private UserId userId;
    private LearnerLevel level;
    private Tags tags;
    private String job;

    private UserProfile(UserProfileId id, UserId userId, LearnerLevel level, Tags tags, String job) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.level = Objects.requireNonNull(level);
        this.tags = Objects.requireNonNull(tags);
        this.job = job;
    }

    //todo 추후 도메인 서비스에서 user가 활성화 상태인지 여부 체크
    public static UserProfile create(UserProfileId id, UserId userId, LearnerLevel level, Tags tags, String job) {
        return new UserProfile(id, userId, level, tags, job);
    }

    //todo 추후 도메인 서비스에서 user가 활성화 상태인지 여부 체크
    public void update(LearnerLevel level, List<Long> tags, String job) {
        this.level = Objects.requireNonNull(level);
        this.tags = this.tags.withTags(tags);
        this.job = job;
    }

    public UserProfileId id() {
        return this.id;
    }

    public UserId userId() {
        return this.userId;
    }

    public LearnerLevel level() {
        return this.level;
    }

    public Tags tags() {
        return this.tags;
    }

    public String job() {
        return this.job;
    }

    @Override
    public UserProfileId getId() {
        return this.id;
    }
}
