package com.lxp.recommend.integration;

import com.lxp.recommend.adapter.out.persistence.*;
import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.result.entity.RecommendResult;
import com.lxp.recommend.domain.user.entity.Level;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepositoryAdapter.class, CourseRepositoryAdapter.class, ResultRepositoryAdapter.class})
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
class RepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private UserRepositoryAdapter userRepository;

    @Autowired
    private CourseRepositoryAdapter courseRepository;

    @Autowired
    private ResultRepositoryAdapter resultRepository;

    @Test
    @DisplayName("User를 저장하고 조회할 수 있다")
    void saveAndFindUser() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .interestTags(List.of(1L, 2L, 3L))
                .level(Level.JUNIOR)
                .enrolledCourseIds(List.of("course-1"))
                .createdCourseIds(List.of())
                .build();

        userRepository.save(user);

        RecommendUser found = userRepository.findById("user-1").orElseThrow();

        assertThat(found.getId()).isEqualTo("user-1");
        assertThat(found.getInterestTags()).containsExactly(1L, 2L, 3L);
        assertThat(found.getLevel()).isEqualTo(Level.JUNIOR);
        assertThat(found.getEnrolledCourseIds()).containsExactly("course-1");
    }

    @Test
    @DisplayName("Status로 User를 조회할 수 있다")
    void findUsersByStatus() {
        userRepository.save(RecommendUser.builder()
                .id("user-1").level(Level.JUNIOR).status(Status.ACTIVE).build());
        userRepository.save(RecommendUser.builder()
                .id("user-2").level(Level.MIDDLE).status(Status.ACTIVE).build());
        userRepository.save(RecommendUser.builder()
                .id("user-3").level(Level.SENIOR).status(Status.INACTIVE).build());

        List<RecommendUser> activeUsers = userRepository.findByStatus(Status.ACTIVE);

        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting(RecommendUser::getId)
                .containsExactlyInAnyOrder("user-1", "user-2");
    }

    @Test
    @DisplayName("Course를 저장하고 조회할 수 있다")
    void saveAndFindCourse() {
        RecommendCourse course = RecommendCourse.builder()
                .id("course-1")
                .tags(List.of(1L, 2L))
                .level(Level.MIDDLE)
                .instructorId("instructor-1")
                .build();

        courseRepository.save(course);

        RecommendCourse found = courseRepository.findById("course-1").orElseThrow();

        assertThat(found.getId()).isEqualTo("course-1");
        assertThat(found.getTags()).containsExactly(1L, 2L);
        assertThat(found.getLevel()).isEqualTo(Level.MIDDLE);
        assertThat(found.getInstructorId()).isEqualTo("instructor-1");
    }

    @Test
    @DisplayName("Instructor ID로 Course를 조회할 수 있다")
    void findCoursesByInstructorId() {
        courseRepository.save(RecommendCourse.builder()
                .id("course-1").level(Level.JUNIOR).instructorId("instructor-1").build());
        courseRepository.save(RecommendCourse.builder()
                .id("course-2").level(Level.MIDDLE).instructorId("instructor-1").build());
        courseRepository.save(RecommendCourse.builder()
                .id("course-3").level(Level.SENIOR).instructorId("instructor-2").build());

        List<RecommendCourse> courses = courseRepository.findByInstructorId("instructor-1");

        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(RecommendCourse::getId)
                .containsExactlyInAnyOrder("course-1", "course-2");
    }

    @Test
    @DisplayName("Result를 저장하고 userId로 조회할 수 있다")
    void saveAndFindResult() {
        RecommendResult result = RecommendResult.builder()
                .userId("user-1")
                .courseIds(List.of("course-1", "course-2", "course-3"))
                .batchId("batch_20260207_0600")
                .build();

        resultRepository.save(result);

        RecommendResult found = resultRepository.findByUserId("user-1").orElseThrow();

        assertThat(found.getUserId()).isEqualTo("user-1");
        assertThat(found.getCourseIds()).containsExactly("course-1", "course-2", "course-3");
        assertThat(found.getBatchId()).isEqualTo("batch_20260207_0600");
    }

    @Test
    @DisplayName("User의 JSONB 필드를 업데이트할 수 있다")
    void updateUserJsonbFields() {
        RecommendUser user = RecommendUser.builder()
                .id("user-1")
                .interestTags(List.of(1L))
                .level(Level.JUNIOR)
                .enrolledCourseIds(List.of())
                .createdCourseIds(List.of())
                .build();

        userRepository.save(user);

        RecommendUser found = userRepository.findById("user-1").orElseThrow();
        found.addEnrolledCourse("course-1");
        found.addEnrolledCourse("course-2");
        userRepository.save(found);

        RecommendUser updated = userRepository.findById("user-1").orElseThrow();

        assertThat(updated.getEnrolledCourseIds()).containsExactly("course-1", "course-2");
    }

    @Test
    @DisplayName("Status별 User 수를 셀 수 있다")
    void countUsersByStatus() {
        userRepository.save(RecommendUser.builder()
                .id("user-1").level(Level.JUNIOR).status(Status.ACTIVE).build());
        userRepository.save(RecommendUser.builder()
                .id("user-2").level(Level.MIDDLE).status(Status.ACTIVE).build());
        userRepository.save(RecommendUser.builder()
                .id("user-3").level(Level.SENIOR).status(Status.INACTIVE).build());

        assertThat(userRepository.countByStatus(Status.ACTIVE)).isEqualTo(2);
        assertThat(userRepository.countByStatus(Status.INACTIVE)).isEqualTo(1);
    }
}
