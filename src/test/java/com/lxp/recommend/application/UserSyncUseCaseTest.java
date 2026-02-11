package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.UserSyncCommand;
import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.application.service.UserSyncService;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserSyncUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventIdempotencyUseCase eventIdempotencyUseCase;

    @InjectMocks
    private UserSyncService userSyncService;

    private UserSyncCommand createCommand;

    @BeforeEach
    void setUp() {
        createCommand = new UserSyncCommand(
                "evt-1",
                "user-1",
                List.of(1L, 2L, 3L),
                "JUNIOR"
        );
    }

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void createUser_success() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(userRepository.existsById("user-1")).willReturn(false);
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        userSyncService.createUser(createCommand);

        ArgumentCaptor<RecommendUser> captor = ArgumentCaptor.forClass(RecommendUser.class);
        then(userRepository).should().save(captor.capture());

        RecommendUser saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo("user-1");
        assertThat(saved.getInterestTags()).containsExactly(1L, 2L, 3L);
        assertThat(saved.getLevel()).isEqualTo(Level.JUNIOR);

        then(eventIdempotencyUseCase).should().markAsProcessed("evt-1");
    }

    @Test
    @DisplayName("이미 존재하는 사용자는 생성을 건너뛴다")
    void createUser_alreadyExists() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(userRepository.existsById("user-1")).willReturn(true);

        userSyncService.createUser(createCommand);

        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("중복 이벤트는 처리하지 않는다")
    void createUser_duplicateEvent() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(true);

        userSyncService.createUser(createCommand);

        then(userRepository).should(never()).save(any());
        then(eventIdempotencyUseCase).should(never()).markAsProcessed(any());
    }

    @Test
    @DisplayName("사용자 정보를 업데이트할 수 있다")
    void updateUser_success() {
        RecommendUser existingUser = RecommendUser.builder()
                .id("user-1")
                .interestTags(List.of(1L))
                .level(Level.JUNIOR)
                .build();

        UserSyncCommand updateCommand = new UserSyncCommand(
                "evt-2",
                "user-1",
                List.of(4L, 5L),
                "MIDDLE"
        );

        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        userSyncService.updateUser(updateCommand);

        assertThat(existingUser.getInterestTags()).containsExactly(4L, 5L);
        assertThat(existingUser.getLevel()).isEqualTo(Level.MIDDLE);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 업데이트할 수 없다")
    void updateUser_notFound() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userSyncService.updateUser(createCommand))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자를 삭제(비활성화)할 수 있다")
    void deleteUser_success() {
        RecommendUser existingUser = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .status(Status.ACTIVE)
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-3")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        userSyncService.deleteUser("evt-3", "user-1");

        assertThat(existingUser.getStatus()).isEqualTo(Status.INACTIVE);
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-3");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제는 건너뛴다")
    void deleteUser_notFound() {
        given(eventIdempotencyUseCase.isDuplicate("evt-3")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        userSyncService.deleteUser("evt-3", "user-1");

        then(userRepository).should(never()).save(any());
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-3");
    }
}
