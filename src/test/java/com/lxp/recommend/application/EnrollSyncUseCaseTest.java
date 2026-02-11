package com.lxp.recommend.application;

import com.lxp.recommend.application.dto.EnrollSyncCommand;
import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.application.service.EnrollSyncService;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.global.exception.BusinessException;
import com.lxp.recommend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollSyncUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventIdempotencyUseCase eventIdempotencyUseCase;

    @InjectMocks
    private EnrollSyncService enrollSyncService;

    @Test
    @DisplayName("수강 등록을 추가할 수 있다")
    void createEnrollment_success() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(new ArrayList<>())
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.of(user));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        EnrollSyncCommand command = new EnrollSyncCommand("evt-1", "user-1", "course-1");
        enrollSyncService.createEnrollment(command);

        assertThat(user.getEnrolledCourseIds()).contains("course-1");
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-1");
    }

    @Test
    @DisplayName("존재하지 않는 사용자에게는 수강 등록을 추가할 수 없다")
    void createEnrollment_userNotFound() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        EnrollSyncCommand command = new EnrollSyncCommand("evt-1", "user-1", "course-1");

        assertThatThrownBy(() -> enrollSyncService.createEnrollment(command))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("수강 등록을 취소할 수 있다")
    void deleteEnrollment_success() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .level(Level.JUNIOR)
                .enrolledCourseIds(new ArrayList<>(List.of("course-1", "course-2")))
                .build();

        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.of(user));
        given(userRepository.save(any(RecommendUser.class))).willAnswer(inv -> inv.getArgument(0));

        EnrollSyncCommand command = new EnrollSyncCommand("evt-2", "user-1", "course-1");
        enrollSyncService.deleteEnrollment(command);

        assertThat(user.getEnrolledCourseIds()).containsExactly("course-2");
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-2");
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 수강 등록 취소는 건너뛴다")
    void deleteEnrollment_userNotFound() {
        given(eventIdempotencyUseCase.isDuplicate("evt-2")).willReturn(false);
        given(userRepository.findById("user-1")).willReturn(Optional.empty());

        EnrollSyncCommand command = new EnrollSyncCommand("evt-2", "user-1", "course-1");
        enrollSyncService.deleteEnrollment(command);

        then(userRepository).should(never()).save(any());
        then(eventIdempotencyUseCase).should().markAsProcessed("evt-2");
    }

    @Test
    @DisplayName("중복 이벤트는 처리하지 않는다")
    void createEnrollment_duplicateEvent() {
        given(eventIdempotencyUseCase.isDuplicate("evt-1")).willReturn(true);

        EnrollSyncCommand command = new EnrollSyncCommand("evt-1", "user-1", "course-1");
        enrollSyncService.createEnrollment(command);

        then(userRepository).should(never()).findById(any());
        then(eventIdempotencyUseCase).should(never()).markAsProcessed(any());
    }
}
