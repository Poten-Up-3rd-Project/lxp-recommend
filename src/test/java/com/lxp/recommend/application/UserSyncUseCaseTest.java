package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.UserSyncCommand;
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

    @InjectMocks
    private UserSyncService userSyncService;

    private UserSyncCommand createCommand;

    @BeforeEach
    void setUp() {
        createCommand = new UserSyncCommand(
                "user-1",
                List.of(1L, 2L, 3L),
                "JUNIOR"
        );
    }

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void createUser_success() {
        given(userRepository.existsById("user-1")).willReturn(false);
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        userSyncService.createUser(createCommand);

        ArgumentCaptor<RecommendUser> captor = ArgumentCaptor.forClass(RecommendUser.class);
        then(userRepository).should().save(captor.capture());

        RecommendUser saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo("user-1");
        assertThat(saved.getInterestTags()).containsExactly(1L, 2L, 3L);
        assertThat(saved.getLevel()).isEqualTo(Level.JUNIOR);
    }

    @Test
    @DisplayName("이미 존재하는 사용자는 생성할 수 없다")
    void createUser_alreadyExists() {
        given(userRepository.existsById("user-1")).willReturn(true);

        assertThatThrownBy(() -> userSyncService.createUser(createCommand))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("사용자 정보를 업데이트할 수 있다")
    void updateUser_success() {
        RecommendUser existingUser = RecommendUser.builder()
                .id("user-1")
                .interestTags(List.of(1L))
                .level(Level.JUNIOR)
                .build();

        given(userRepository.findById("user-1")).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        UserSyncCommand updateCommand = new UserSyncCommand(
                "user-1",
                List.of(4L, 5L),
                "MIDDLE"
        );

        userSyncService.updateUser(updateCommand);

        assertThat(existingUser.getInterestTags()).containsExactly(4L, 5L);
        assertThat(existingUser.getLevel()).isEqualTo(Level.MIDDLE);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 업데이트할 수 없다")
    void updateUser_notFound() {
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

        given(userRepository.findById("user-1")).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        userSyncService.deleteUser("user-1");

        assertThat(existingUser.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 삭제할 수 없다")
    void deleteUser_notFound() {
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userSyncService.deleteUser("user-1"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
