package com.lxp.content.course.infrastructure.adapter;

import com.lxp.content.config.JpaTestConfig;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
import com.lxp.content.course.domain.model.id.CourseUUID;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.infrastructure.persistence.adapter.CoursePersistenceAdapter;
import com.lxp.content.course.infrastructure.persistence.mapper.CourseEntityMapper;
import com.lxp.content.course.infrastructure.persistence.repository.CourseJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(classes = JpaTestConfig.class)
@Import({CoursePersistenceAdapter.class, CourseEntityMapper.class})
@Transactional
@DisplayName("CoursePersistenceAdapter 테스트")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CoursePersistenceAdapterIntegrationTest {

    @Autowired
    private CoursePersistenceAdapter adapter;

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    @Test
    @DisplayName("실제 DB에 Course를 저장하고 조회할 수 있다")
    void save_ShouldPersistCourseInDatabase() {
        // Given
        CourseUUID courseUUID = new CourseUUID(UUID.randomUUID().toString());
        InstructorUUID instructorUUID = new InstructorUUID(UUID.randomUUID().toString());

        Course course = Course.create(
                courseUUID,
                instructorUUID,
                "https://example.com/thumbnail.jpg",
                "통합 테스트 강의",
                "통합 테스트 설명",
                CourseDifficulty.JUNIOR,
                new CourseSections(Collections.emptyList()),
                new CourseTags(List.of(new TagId(1L), new TagId(2L)))
        );

        // When
        Course savedCourse = adapter.save(course);

        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.uuid()).isEqualTo(courseUUID);

        // Then:
        var entity = courseJpaRepository.findById(savedCourse.id());
        assertThat(entity).isPresent();
        assertThat(entity.get().getTitle()).isEqualTo("통합 테스트 강의");
        assertThat(entity.get().getCreatedAt()).isNotNull();
    }
}