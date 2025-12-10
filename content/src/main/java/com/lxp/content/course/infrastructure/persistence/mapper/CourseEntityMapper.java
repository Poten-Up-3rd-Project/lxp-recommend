package com.lxp.content.course.infrastructure.persistence.mapper;

import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.id.CourseUUID;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.id.SectionUUID;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.infrastructure.persistence.entity.CourseJpaEntity;
import com.lxp.content.course.infrastructure.persistence.entity.LectureJpaEntity;
import com.lxp.content.course.infrastructure.persistence.entity.SectionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseEntityMapper {

    public CourseJpaEntity toEntity(Course course) {
        CourseJpaEntity courseEntity = CourseJpaEntity.builder()
                .uuid(course.uuid().value())
                .instructorUUID(course.instructorUUID().value())
                .title(course.title().value())
                .description(course.description() != null ? course.description().value() : null)
                .thumbnailUrl(course.thumbnailUrl())
                .difficulty(course.difficulty())
                .tags(course.tags().values().stream()
                                .map(TagId::value)
                                .toList()
                )
                .build();

        for (Section sectionDomain : course.sections().values()) {
            SectionJpaEntity sectionEntity = toSectionEntity(sectionDomain);
            courseEntity.addSection(sectionEntity);
        }

        return courseEntity;
    }

    // lecture 이 empty일수있음
    private SectionJpaEntity toSectionEntity(Section section) {
        SectionJpaEntity sectionEntity = new SectionJpaEntity(
                section.uuid().value(),
                section.title(),
                section.order()
        );

        for (Lecture lectureDomain : section.lectures().values()) {
            LectureJpaEntity lectureEntity = toLectureEntity(lectureDomain);
            sectionEntity.addLecture(lectureEntity);
        }

        return sectionEntity;
    }

    private LectureJpaEntity toLectureEntity(Lecture lecture) {
        return new LectureJpaEntity(
                lecture.uuid().value(),
                lecture.title(),
                lecture.duration().seconds(),
                lecture.order(),
                lecture.videoUrl()
        );
    }


    public Course toDomain(CourseJpaEntity entity) {
        return Course.reconstruct(
                entity.getId(),
                new CourseUUID(entity.getUuid()),
                new InstructorUUID(entity.getInstructorUUID()),
                entity.getThumbnailUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDifficulty(),
                mapSectionsToDomain(entity.getSections()),
                mapTagsToDomain(entity.getTags()),
                entity.getCreatedAt().toInstant(ZoneOffset.UTC),
                entity.getUpdatedAt().toInstant(ZoneOffset.UTC)
        );
    }

    private CourseSections mapSectionsToDomain(List<SectionJpaEntity> sectionEntities) {
        List<Section> sections = sectionEntities.stream()
                .map(this::toSectionDomain)
                .collect(Collectors.toList());

        return new CourseSections(sections);
    }

    private Section toSectionDomain(SectionJpaEntity entity) {
        return Section.reconstruct(
                entity.getId(),
                new SectionUUID(entity.getUuid()),
                entity.getTitle(),
                entity.getOrder(),
                mapLectureToDomain(entity.getLectures())
        );
    }

    private SectionLectures mapLectureToDomain(List<LectureJpaEntity> lectureEntities) {
        List<Lecture> lectures = lectureEntities.stream()
                .map(this::toLectureDomain)
                .toList();

        return new SectionLectures(lectures);
    }

    private Lecture toLectureDomain(LectureJpaEntity entity) {
        return Lecture.reconstruct(
                new LectureUUID(entity.getUuid()),
                entity.getId(),
                entity.getTitle(),
                new LectureDuration(entity.getDurationSeconds()),
                entity.getOrder(),
                entity.getVideoUrl()
        );
    }

    private CourseTags mapTagsToDomain(List<Long> tags) {
        List<TagId> tagIds = tags.stream()
                .map(TagId::new)
                .collect(Collectors.toList());
        return CourseTags.of(tagIds);
    }
}
