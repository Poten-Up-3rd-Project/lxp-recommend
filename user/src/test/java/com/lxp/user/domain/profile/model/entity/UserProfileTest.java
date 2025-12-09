package com.lxp.user.domain.profile.model.entity;

import com.lxp.user.domain.common.model.vo.UserId;
import com.lxp.user.domain.profile.model.vo.LearnerLevel;
import com.lxp.user.domain.profile.model.vo.Tags;
import com.lxp.user.domain.profile.model.vo.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class UserProfileTest {

    private UserProfileId userProfileId;
    private UserId userId;
    private LearnerLevel initialLevel;
    private Tags initialTags;
    private String initialJob;

    @BeforeEach
    void setUp() {
        userProfileId = new UserProfileId(100L);
        userId = UserId.create();
        initialLevel = LearnerLevel.EXPERT;
        initialTags = Mockito.mock(Tags.class);
        initialJob = "Software Engineer";
    }

    // --- UserProfile.create() 테스트 ---

    @Test
    @DisplayName("UserProfile 생성 테스트")
    void create_ShouldCreateUserProfileWithGivenValues() {
        // when
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);

        // then
        assertAll(
            () -> assertNotNull(userProfile, "UserProfile 객체는 null이 아니어야 합니다."),
            () -> assertEquals(userProfileId, userProfile.id(), "ID는 일치해야 합니다."),
            () -> assertEquals(userId, userProfile.userId(), "UserId는 일치해야 합니다."),
            () -> assertEquals(initialLevel, userProfile.level(), "LearnerLevel은 일치해야 합니다."),
            () -> assertEquals(initialTags, userProfile.tags(), "Tags는 일치해야 합니다."),
            () -> assertEquals(initialJob, userProfile.job(), "Job은 일치해야 합니다."),
            () -> assertEquals(userProfileId, userProfile.getId(), "getId()는 올바른 ID를 반환해야 합니다.")
        );
    }

    @Test
    @DisplayName("UserProfile 생성 시 필수 필드 누락 검증 (Null Check)")
    void create_ShouldThrowNPEWhenRequiredFieldsAreNull() {
        // given
        UserProfileId nullId = null;
        UserId nullUserId = null;
        LearnerLevel nullLevel = null;
        Tags nullTags = null;

        // when & then
        assertAll(
            () -> assertThrows(NullPointerException.class,
                () -> UserProfile.create(nullId, userId, initialLevel, initialTags, initialJob),
                "ID가 null이면 NullPointerException이 발생해야 합니다."
            ),

            () -> assertThrows(NullPointerException.class,
                () -> UserProfile.create(userProfileId, nullUserId, initialLevel, initialTags, initialJob),
                "UserId가 null이면 NullPointerException이 발생해야 합니다."
            ),

            () -> assertThrows(NullPointerException.class,
                () -> UserProfile.create(userProfileId, userId, nullLevel, initialTags, initialJob),
                "LearnerLevel이 null이면 NullPointerException이 발생해야 합니다."
            ),

            () -> assertThrows(NullPointerException.class,
                () -> UserProfile.create(userProfileId, userId, initialLevel, nullTags, initialJob),
                "Tags가 null이면 NullPointerException이 발생해야 합니다."
            )
        );
    }

    // --- UserProfile.update() 테스트 ---

    @Test
    @DisplayName("UserProfile 정보 업데이트 테스트")
    void update_ShouldUpdateLevelTagsAndJob() {
        // given
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);

        LearnerLevel newLevel = LearnerLevel.MIDDLE;
        List<Long> newTagsList = Arrays.asList(5L, 6L, 7L);
        String newJob = "Lead Developer";

        Tags updatedTags = Mockito.mock(Tags.class);
        given(initialTags.withTags(newTagsList)).willReturn(updatedTags);

        // when
        userProfile.update(newLevel, newTagsList, newJob);

        // then
        assertAll(
            () -> assertEquals(newLevel, userProfile.level(), "LearnerLevel이 업데이트되어야 합니다."),
            () -> assertEquals(updatedTags, userProfile.tags(), "Tags는 withTags()의 결과를 받아 업데이트되어야 합니다."),
            () -> assertEquals(newJob, userProfile.job(), "Job이 업데이트되어야 합니다.")
        );

        then(initialTags).should().withTags(newTagsList);
    }

    @Test
    @DisplayName("UserProfile 업데이트 시 LearnerLevel이 null인 경우 예외 발생")
    void update_ShouldThrowNPEWhenLevelIsNull() {
        // given
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);
        List<Long> tags = Collections.emptyList();
        String job = "Job";

        // when & then
        assertThrows(NullPointerException.class, () -> userProfile.update(null, tags, job),
            "LearnerLevel이 null이면 NullPointerException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("UserProfile 업데이트 시 tags가 null인 경우")
    void update_ShouldHandleNullTagsList() {
        // given
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);
        LearnerLevel newLevel = LearnerLevel.MIDDLE;
        String newJob = "New Job";

        Tags tagsHandlingNull = Mockito.mock(Tags.class);
        given(initialTags.withTags(null)).willReturn(tagsHandlingNull);

        // when & then
        assertDoesNotThrow(() -> userProfile.update(newLevel, null, newJob),
            "Tags 리스트가 null이어도 update 메소드 자체에서 바로 예외가 발생하면 안 됩니다.");

        // then (상태 검증)
        assertAll(
            () -> assertEquals(newLevel, userProfile.level(), "LearnerLevel이 업데이트되어야 합니다."),
            () -> assertEquals(tagsHandlingNull, userProfile.tags(), "Tags 필드가 Mock 객체가 반환한 값으로 업데이트되어야 합니다.")
        );

        then(initialTags).should().withTags(null);
    }

    @Test
    @DisplayName("UserProfile 업데이트 시 job이 null인 경우")
    void update_ShouldAcceptNullJob() {
        // given
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);
        LearnerLevel newLevel = LearnerLevel.EXPERT;
        List<Long> tags = Arrays.asList(1L);

        // when
        userProfile.update(newLevel, tags, null);

        // then
        assertNull(userProfile.job(), "Job은 null로 업데이트될 수 있어야 합니다.");
    }

    @Test
    @DisplayName("Getter 메소드 검증")
    void getters_ShouldReturnCorrectValues() {
        // when
        UserProfile userProfile = UserProfile.create(userProfileId, userId, initialLevel, initialTags, initialJob);

        // then
        assertAll(
            () -> assertEquals(userProfileId, userProfile.id(), "id()는 정확한 UserProfileId를 반환해야 합니다."),
            () -> assertEquals(userId, userProfile.userId(), "userId()는 정확한 UserId를 반환해야 합니다."),
            () -> assertEquals(initialLevel, userProfile.level(), "level()은 정확한 LearnerLevel을 반환해야 합니다."),
            () -> assertEquals(initialTags, userProfile.tags(), "tags()는 정확한 Tags를 반환해야 합니다."),
            () -> assertEquals(initialJob, userProfile.job(), "job()은 정확한 job을 반환해야 합니다."),
            () -> assertEquals(userProfileId, userProfile.getId(), "getId()는 정확한 UserProfileId를 반환해야 합니다.")
        );
    }
}
