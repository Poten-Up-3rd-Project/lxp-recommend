package com.lxp.auth.domain.local.model.entity;

import com.lxp.auth.domain.common.model.vo.UserId;
import com.lxp.auth.domain.local.model.vo.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("LocalAuth 엔티티 테스트")
class LocalAuthTest {

    private static final String LOGIN_IDENTIFIER = "testuser@example.com";
    private static final String ORIGINAL_PASSWORD_VALUE = "originalHash";
    private static final String NEW_PASSWORD_VALUE = "newHash";

    private UserId mockUserId;
    private HashedPassword originalHashedPassword;
    private HashedPassword newHashedPassword;
    private LocalAuth registeredAuth; // register()로 생성된 객체
    private OffsetDateTime fixedCreatedAt;
    private OffsetDateTime fixedModifiedAt;

    @BeforeEach
    void setUp() {
        // Given 상태에 필요한 기본 객체들을 매 테스트 메서드 실행 전에 초기화합니다.
        mockUserId = UserId.create();
        originalHashedPassword = new HashedPassword(ORIGINAL_PASSWORD_VALUE);
        newHashedPassword = new HashedPassword(NEW_PASSWORD_VALUE);
        fixedCreatedAt = OffsetDateTime.of(2023, 1, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        fixedModifiedAt = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);

        registeredAuth = LocalAuth.register(LOGIN_IDENTIFIER, originalHashedPassword);
    }

    // ---

    @Test
    @DisplayName("GIVEN 필드들이 초기화되면 WHEN register 메서드를 호출하면 THEN 새로운 LocalAuth 객체가 생성된다.")
    void register_createsNewLocalAuth() {
        // When: register 메서드 호출 (setUp()에서 이미 한 번 호출되었지만, 필드 값 검증을 위해 재사용)
        LocalAuth localAuth = LocalAuth.register(LOGIN_IDENTIFIER, originalHashedPassword);

        // Then: LocalAuth 객체 생성 및 필드 검증
        assertAll(
            () -> assertThat(localAuth).isNotNull(),
            () -> assertThat(localAuth.userId()).isNotNull(),
            () -> assertThat(localAuth.loginIdentifier()).isEqualTo(LOGIN_IDENTIFIER),
            () -> assertThat(localAuth.hashedPassword().value()).isEqualTo(ORIGINAL_PASSWORD_VALUE),
            () -> assertThat(localAuth.createdAt()).isBeforeOrEqualTo(OffsetDateTime.now()),
            () -> assertThat(localAuth.lastPasswordModifiedAt()).isNull()
        );
    }

    // ---

    @Test
    @DisplayName("GIVEN 모든 필드 값이 초기화되면 WHEN of 메서드를 호출하면 THEN 기존 LocalAuth 객체가 정확히 로딩된다.")
    void of_createsExistingLocalAuth() {
        // When: of 메서드 호출
        LocalAuth localAuth = LocalAuth.of(mockUserId, LOGIN_IDENTIFIER, originalHashedPassword, fixedCreatedAt, fixedModifiedAt);

        // Then: LocalAuth 객체 생성 및 필드 검증
        assertAll(
            () -> assertThat(localAuth.userId()).isEqualTo(mockUserId),
            () -> assertThat(localAuth.loginIdentifier()).isEqualTo(LOGIN_IDENTIFIER),
            () -> assertThat(localAuth.hashedPassword()).isEqualTo(originalHashedPassword),
            () -> assertThat(localAuth.createdAt()).isEqualTo(fixedCreatedAt),
            () -> assertThat(localAuth.lastPasswordModifiedAt()).isEqualTo(fixedModifiedAt)
        );
    }

    // ---

    @Test
    @DisplayName("GIVEN 등록된 LocalAuth 객체가 주어지고 WHEN updatePassword를 호출하면 THEN 비밀번호와 수정 시간이 갱신된다.")
    void updatePassword_updatesPasswordAndModifiedTime() {
        // When: updatePassword 메서드 호출
        registeredAuth.updatePassword(newHashedPassword);

        // Then: 비밀번호와 수정 시간이 갱신되었는지 확인
        assertAll(
            () -> assertThat(registeredAuth.hashedPassword().value()).isEqualTo(NEW_PASSWORD_VALUE),
            () -> assertThat(registeredAuth.lastPasswordModifiedAt()).isNotNull(),
            () -> assertThat(registeredAuth.lastPasswordModifiedAt()).isAfterOrEqualTo(registeredAuth.createdAt())
        );
    }

    @Test
    @DisplayName("GIVEN LocalAuth 객체가 주어지고 WHEN updatePassword에 null을 전달하면 THEN NullPointerException이 발생한다.")
    void updatePassword_withNull_throwsException() {
        // When & Then: updatePassword(null) 호출 시 예외 발생
        assertThrows(NullPointerException.class, () -> {
            registeredAuth.updatePassword(null);
        }, "비밀번호는 null일 수 없습니다.");
    }

    // ---

    @Test
    @DisplayName("GIVEN LocalAuth 객체가 주어지고 WHEN matchesId(UserId)를 호출하면 THEN 동일한 UserId에 대해 true를 반환한다.")
    void matchesId_withUserId_returnsTrueForSameId() {
        // Given: mockUserId로 of()를 사용하여 로딩된 객체
        LocalAuth localAuth = LocalAuth.of(mockUserId, LOGIN_IDENTIFIER, originalHashedPassword, fixedCreatedAt, null);

        // When & Then: 동일한 UserId로 검증
        assertThat(localAuth.matchesId(mockUserId)).isTrue();
    }

    @Test
    @DisplayName("GIVEN LocalAuth 객체가 주어지고 WHEN matchesId(String)를 호출하면 THEN 동일한 ID 문자열에 대해 true를 반환한다. (UUID 환경)")
    void matchesId_withStringId_returnsTrueForSameId_UUID() {
        // Given: LocalAuth.register()로 객체를 생성합니다.
        LocalAuth localAuth = LocalAuth.register(LOGIN_IDENTIFIER, originalHashedPassword);
        String generatedIdString = localAuth.userId().asString();

        // When & Then: 생성된 ID 문자열로 검증
        assertThat(localAuth.matchesId(generatedIdString)).isTrue();
    }
}
