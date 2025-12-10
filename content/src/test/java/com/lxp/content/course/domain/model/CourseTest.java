package com.lxp.content.course.domain.model;

import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
import com.lxp.content.course.domain.model.id.*;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CourseTest {
    private Course course;
    private CourseSections validSections;
    private CourseTags validTags;

    @BeforeEach
    void setUp() {
        Section section = Section.create(
                "섹션 1",
                new SectionUUID("section-123"),
                1
        );

        validSections = new CourseSections(List.of(section));
        validTags = CourseTags.of(List.of(new TagId(1L)));

        CourseUUID courseUUID = new CourseUUID("course-123");
        course = Course.create(
                courseUUID,
                new InstructorUUID("instructor-456"),
                "thumbnail.png",
                "Java 기초",
                "자바 기초 강의입니다",
                CourseDifficulty.JUNIOR,
                validSections,
                validTags
        );
    }

    @Nested
    @DisplayName("Course create")
    class CreateCourse {

        @Test
        @DisplayName("normal create Course")
        void createCourse() {
            assertThat(course.instructorUUID().value()).isEqualTo("instructor-456");
            assertThat(course.title().value()).isEqualTo("Java 기초");
            assertThat(course.description().value()).isEqualTo("자바 기초 강의입니다");
            assertThat(course.difficulty()).isEqualTo(CourseDifficulty.JUNIOR);
            assertThat(course.sections().values()).hasSize(1);
        }

        @Test
        @DisplayName("exception title is null")
        void createCourseWithNullTitle() {
            CourseUUID courseUUID = new CourseUUID("course-123");
            assertThatThrownBy(() -> Course.create(
                    courseUUID,
                    new InstructorUUID("instructor-456"),
                    null,
                    null,
                    "description",
                    CourseDifficulty.JUNIOR,
                    validSections,
                    validTags
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("exception courseDifficulty is null")
        void createCourseWithNullCourseDifficulty() {
            CourseUUID courseUUID = new CourseUUID("course-123");
            assertThatThrownBy(() -> Course.create(
                    courseUUID,
                    new InstructorUUID("instructor-456"),
                    "제목",
                    "설명",
                    null,
                    null,
                    validSections,
                    validTags)
            ).isInstanceOf(NullPointerException.class);
        }
    }


    @Nested
    @DisplayName("Course info update")
    class UpdateCourseInfo {

        @Test
        @DisplayName("update title")
        void rename() {
            course.rename("Spring 기초");

            assertThat(course.title().value()).isEqualTo("Spring 기초");
        }

        @Test
        @DisplayName("update description")
        void changeDescription() {
            course.changeDescription("새로운 설명");

            assertThat(course.description().value()).isEqualTo("새로운 설명");
        }

        @Test
        @DisplayName("update difficulty")
        void changeDifficulty() {
            course.changeDifficulty(CourseDifficulty.MIDDLE);

            assertThat(course.difficulty()).isEqualTo(CourseDifficulty.MIDDLE);
        }
    }

    @Nested
    @DisplayName("manage Section")
    class ManageSection {

        @Test
        @DisplayName("add Section")
        void addSection() {
            SectionUUID sectionUUID = new SectionUUID("section-new-999");
            course.addSection(sectionUUID,"2장. 추가된 챕터");

            // 기존 1개 + 추가 1개 = 2개
            assertThat(course.sections().values()).hasSize(2);
            assertThat(course.sections().values().get(1).uuid()).isEqualTo(sectionUUID);
            assertThat(course.sections().values().get(1).title()).isEqualTo("2장. 추가된 챕터");
            assertThat(course.sections().values().get(1).order()).isEqualTo(2);
        }

        @Test
        @DisplayName("apply order when adding multiple Sections")
        void addMultipleSections() {
            // [수정] 이미 존재하는 section-123(1번) 뒤에 붙음
            course.addSection( new SectionUUID("section-new-2"), "2장");
            course.addSection( new SectionUUID("section-new-3"), "3장");
            course.addSection( new SectionUUID("section-new-4"), "4장");

            // 기존 1개 + 추가 3개 = 4개
            assertThat(course.sections().values()).hasSize(4);
            assertThat(course.sections().values().get(1).order()).isEqualTo(2);
            assertThat(course.sections().values().get(2).order()).isEqualTo(3);
            assertThat(course.sections().values().get(3).order()).isEqualTo(4);
        }

        @Test
        @DisplayName("exception when adding duplicate Section UUID")
        void addDuplicateSection() {
            // [수정] setUp에 이미 있는 section-123을 중복 추가 시도
            SectionUUID duplicateUUID = new SectionUUID("section-123");

            assertThatThrownBy(() -> course.addSection(duplicateUUID,"중복 섹션"))
                    .isInstanceOf(CourseException.class);
        }

        @Test
        @DisplayName("remove Section")
        void removeSection() {
            SectionUUID targetUUID = new SectionUUID("section-123");
            course.removeSection(targetUUID);

            assertThat(course.sections().values()).hasSize(0);
        }

        @Test
        @DisplayName("reorder Sections after removal")
        void reorderAfterRemove() {
            // section-123은 이미 있음 (1장)
            SectionUUID sectionUUID2 = new SectionUUID("section-234");
            SectionUUID sectionUUID3 = new SectionUUID("section-345");

            course.addSection(sectionUUID2,"2장");
            course.addSection(sectionUUID3,"3장");

            // 2장 삭제 (중간 삭제)
            course.removeSection(sectionUUID2);

            assertThat(course.sections().values()).hasSize(2);

            // 1장 확인
            assertThat(course.sections().values().get(0).title()).isEqualTo("섹션 1"); // setUp에서 만든 제목
            assertThat(course.sections().values().get(0).order()).isEqualTo(1);

            // 3장이 2번 순서로 당겨졌는지 확인
            assertThat(course.sections().values().get(1).title()).isEqualTo("3장");
            assertThat(course.sections().values().get(1).order()).isEqualTo(2);
        }

        @Test
        @DisplayName("update title of Section")
        void renameSection() {
            // [수정] addSection 하지 않고 기존 section-123 사용
            SectionUUID sectionUUID = new SectionUUID("section-123");

            course.renameSection(sectionUUID, "1장. 수정됨");

            assertThat(course.sections().values().get(0).title()).isEqualTo("1장. 수정됨");
        }

        @Test
        @DisplayName("exception when renaming non-existent Section")
        void renameSectionNotFound() {
            assertThatThrownBy(() -> course.renameSection(new SectionUUID("section-9999"), "제목"))
                    .isInstanceOf(CourseException.class);
        }

        @Test
        @DisplayName("reorder Sections")
        void reorderSection() {
            // section-123 (1번) 이미 있음
            SectionUUID sectionUUID2 = new SectionUUID("section-234");
            SectionUUID sectionUUID3 = new SectionUUID("section-345");

            course.addSection(sectionUUID2,"2장");
            course.addSection(sectionUUID3,"3장");

            // 3번 섹션을 1번 자리로 이동
            course.reorderSection(sectionUUID3, 1);

            assertThat(course.sections().values().get(0).title()).isEqualTo("3장");
            assertThat(course.sections().values().get(1).title()).isEqualTo("섹션 1");
            assertThat(course.sections().values().get(2).title()).isEqualTo("2장");
        }
    }

    @Nested
    @DisplayName("Lecture manage")
    class ManageLecture {

        private SectionUUID sectionUUID;

        @BeforeEach
        void setUp() {
            sectionUUID = new SectionUUID("section-123");
        }

        @Test
        @DisplayName("add lecture")
        void addLecture() {

            LectureUUID lectureId = new LectureUUID("lecture-123");
            course.addLecture(
                    sectionUUID,
                    lectureId,
                    "1-1. 소개",
                    new LectureDuration(600),
                    "https://video.com/1"
            );

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values()).hasSize(1);
            assertThat(section.lectures().values().get(0).title()).isEqualTo("1-1. 소개");
            assertThat(section.lectures().values().get(0).duration().seconds()).isEqualTo(600);
        }

        @Test
        @DisplayName("apply order when adding multiple Lectures")
        void addMultipleLectures() {
            LectureUUID lectureUUID = new LectureUUID("lecture-123");
            LectureUUID lectureUUID2 = new LectureUUID("lecture-234");
            LectureUUID lectureUUID3 = new LectureUUID("lecture-345");

            course.addLecture(sectionUUID, lectureUUID,"1-1", new LectureDuration(300), "url1");
            course.addLecture(sectionUUID, lectureUUID2,"1-2", new LectureDuration(400), "url2");
            course.addLecture(sectionUUID, lectureUUID3, "1-3", new LectureDuration(500), "url3");

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values()).hasSize(3);
            assertThat(section.lectures().values().get(0).order()).isEqualTo(1);
            assertThat(section.lectures().values().get(1).order()).isEqualTo(2);
            assertThat(section.lectures().values().get(2).order()).isEqualTo(3);
        }

        @Test
        @DisplayName("exception when adding duplicate Lecture ID")
        void addDuplicateLecture() {
            LectureUUID lectureId = new LectureUUID("lecture-123");
            course.addLecture(sectionUUID, lectureId, "1-1", new LectureDuration(300), "url");

            assertThatThrownBy(() ->
                    course.addLecture(sectionUUID,lectureId,"중복", new LectureDuration(300), "url")
            ).isInstanceOf(CourseException.class);
        }

        @Test
        @DisplayName("exception when adding Lecture to non-existent Section")
        void addLectureToNonExistentSection() {
            assertThatThrownBy(() ->
                    course.addLecture(
                            new SectionUUID("section-999"),
                            new LectureUUID("lecture-0000"),
                            "제목",
                            new LectureDuration(300),
                            "url"
                    )
            ).isInstanceOf(CourseException.class);
        }

        @Test
        @DisplayName("remove Lecture")
        void removeLecture() {
            LectureUUID lectureId = new LectureUUID("lecture-123");
            course.addLecture(sectionUUID, lectureId,"1-1", new LectureDuration(300), "url");

            course.removeLecture(sectionUUID, lectureId);

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values()).isEmpty();
        }

        @Test
        @DisplayName("reorder Lectures after removal")
        void reorderAfterRemoveLecture() {
            LectureUUID lectureUUID = new LectureUUID("lecture-123");
            LectureUUID lectureUUID2 = new LectureUUID("lecture-234");
            LectureUUID lectureUUID3 = new LectureUUID("lecture-345");
            course.addLecture(sectionUUID, lectureUUID,"1-1", new LectureDuration(300), "url1");
            course.addLecture(sectionUUID, lectureUUID2,"1-2", new LectureDuration(400), "url2");
            course.addLecture(sectionUUID, lectureUUID3, "1-3", new LectureDuration(500), "url3");

            course.removeLecture(sectionUUID, lectureUUID2);

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values()).hasSize(2);
            assertThat(section.lectures().values().get(0).title()).isEqualTo("1-1");
            assertThat(section.lectures().values().get(0).order()).isEqualTo(1);
            assertThat(section.lectures().values().get(1).title()).isEqualTo("1-3");
            assertThat(section.lectures().values().get(1).order()).isEqualTo(2);
        }

        @Test
        @DisplayName("rename Lecture")
        void renameLecture() {
            LectureUUID lectureUUID = new LectureUUID("lecture-123");
            course.addLecture(sectionUUID, lectureUUID,"1-1", new LectureDuration(300), "url");

            course.renameLecture(sectionUUID, lectureUUID, "1-1. 수정됨");

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values().get(0).title()).isEqualTo("1-1. 수정됨");
        }

        @Test
        @DisplayName("update Lecture video URL")
        void changeLectureVideoUrl() {
            LectureUUID lectureUUID = new LectureUUID("lecture-123");
            course.addLecture(sectionUUID, lectureUUID,"1-1", new LectureDuration(300), "old-url");

            course.changeLectureVideoUrl(sectionUUID, lectureUUID, "new-url");

            Section section = course.sections().values().get(0);
            assertThat(section.lectures().values().get(0).videoUrl()).isEqualTo("new-url");
        }
    }

    @Nested
    @DisplayName("manage tags")
    class ManageTags {

        @Test
        @DisplayName("add tag")
        void addTag() {
            course.addTag(new TagId(1L));

            assertThat(course.hasTag(new TagId(1L))).isTrue();
        }

        @Test
        @DisplayName("add multiple tags")
        void addMultipleTags() {
            TagId tag1 = new TagId(1L);
            TagId tag2 = new TagId(2L);
            TagId tag3 = new TagId(3L);

            course.addTag(tag1);
            course.addTag(tag2);
            course.addTag(tag3);

            assertThat(course.hasTag(tag1)).isTrue();
            assertThat(course.hasTag(tag2)).isTrue();
            assertThat(course.hasTag(tag3)).isTrue();
        }

        @Test
        @DisplayName("remove tag")
        void removeTag() {
            TagId tagId = new TagId(2L);

            course.addTag(tagId);

            course.removeTag(tagId);

            assertThat(course.hasTag(tagId)).isFalse();
        }

        @Test
        @DisplayName("remove non-existent tag does not throw exception")
        void removeNonExistentTag() {
            TagId tagId = new TagId(9999L);

            assertThatCode(() -> course.removeTag(tagId))
                    .doesNotThrowAnyException();
        }
    }


    @Nested
    @DisplayName("Course 총 재생시간 계산")
    class CalculateDuration {

        @Test
        @DisplayName("Lecture가 없으면 기본 duration을 반환한다")
        void emptyCourseDuration() {
            assertThat(course.totalDuration().seconds()).isEqualTo(1);
        }

        @Test
        @DisplayName("모든 Lecture의 duration 합계를 반환한다")
        void calculateTotalDuration() {
            SectionUUID section1 = new SectionUUID("section-123");
            SectionUUID section2 = new SectionUUID("section-234");

            LectureUUID lectureUUID = new LectureUUID("lecture-123");
            LectureUUID lectureUUID2 = new LectureUUID("lecture-234");
            LectureUUID lectureUUID3 = new LectureUUID("lecture-345");

            // section1은 이미 있으므로 추가하지 않음
            course.addSection(section2, "2장");

            course.addLecture(section1, lectureUUID, "1-1", new LectureDuration(600), "url");
            course.addLecture(section1, lectureUUID2, "1-2", new LectureDuration(900), "url");
            course.addLecture(section2, lectureUUID3, "2-1", new LectureDuration(1200), "url");

            // 600 + 900 + 1200 = 2700초
            assertThat(course.totalDuration().seconds()).isEqualTo(2700);
        }
    }

}