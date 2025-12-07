package com.lxp.content.course.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.domain.model.id.CourseUUID;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.id.SectionUUID;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.model.vo.CourseDescription;
import com.lxp.content.course.domain.model.vo.CourseTitle;
import com.lxp.content.course.domain.model.vo.duration.CourseDuration;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;

import java.time.Instant;
import java.util.Objects;

public class Course extends AggregateRoot<CourseUUID> {
    private final Long id;
    private final CourseUUID uuid;
    private final InstructorUUID instructorUUID;
    private String thumbnailUrl;
    private CourseTitle title;
    private CourseDescription description;
    private CourseSections sections;
    private CourseDifficulty difficulty;
    private CourseTags tags;
    private Instant createdAt;
    private Instant updatedAt;

    private Course(
            Long id,
            CourseUUID uuid,
            InstructorUUID instructorUUID,
            String thumbnailUrl,
            CourseTitle title,
            CourseDescription description,
            CourseDifficulty difficulty,
            CourseSections sections,
            CourseTags tags,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.uuid = uuid;
        this.instructorUUID = Objects.requireNonNull(instructorUUID);
        this.thumbnailUrl = thumbnailUrl;
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.difficulty = Objects.requireNonNull(difficulty);
        this.sections = Objects.requireNonNull(sections);
        this.tags = Objects.requireNonNull(tags);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Course create(
            InstructorUUID instructorUUID,
            CourseUUID uuid,
            String thumbnailUrl,
            String title,
            String description,
            CourseDifficulty difficulty,
            CourseSections sections,
            CourseTags tags)
    {
        return new Course(
                null,
                uuid,
                instructorUUID,
                thumbnailUrl,
                CourseTitle.of(title),
                CourseDescription.of(description),
                difficulty,
                sections,
                tags,
                null,
                null
        );
    }

    public static Course reconstruct(
            Long id,
            CourseUUID uuid,
            InstructorUUID instructorUUID,
            String thumbnailUrl,
            String title,
            String description,
            CourseDifficulty difficulty,
            CourseSections sections,
            CourseTags tags,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Course(
                id,
                uuid,
                instructorUUID,
                thumbnailUrl,
                CourseTitle.of(title),
                CourseDescription.of(description),
                difficulty,
                sections,
                tags,
                createdAt,
                updatedAt
                );
    }

    //setters
    public void rename(String title) {
        this.title = CourseTitle.of(Objects.requireNonNull(title,"title cannot be null"));
    }

    public void changeDescription(String description) {
        this.description = CourseDescription.of(description);
    }

    public void changeDifficulty(CourseDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void changeThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    //section
    public void addSection(SectionUUID uuid, String title) {
        this.sections = sections.addSection(uuid, title);
    }

    public void removeSection(SectionUUID uuid) {
        this.sections = sections.removeSection(uuid);
    }

    public void renameSection(SectionUUID uuid, String title) {
        this.sections = sections.renameSection(uuid, title);
    }

    public void reorderSection(SectionUUID uuid, int newOrder) {
        this.sections = sections.reorderSection(uuid, newOrder);
    }

    //lecture
    public void addLecture(
            SectionUUID sectionUUID,
            LectureUUID lectureUUID,
            String title,
            LectureDuration duration,
            String videoUrl
    ) {
        this.sections = sections.addLecture(sectionUUID, lectureUUID, title, duration, videoUrl);
    }

    public void removeLecture(SectionUUID sectionUUID, LectureUUID lectureUUID) {
        this.sections = sections.removeLecture(sectionUUID, lectureUUID);
    }

    public void renameLecture(SectionUUID sectionUUID, LectureUUID lectureUUID, String newTitle) {
        this.sections = sections.renameLecture(sectionUUID, lectureUUID, newTitle);
    }

    public void changeLectureVideoUrl(SectionUUID sectionUUID, LectureUUID lectureUUID, String url) {
        this.sections = sections.changeLectureVideoUrl(sectionUUID, lectureUUID, url);
    }

    // tag
    public void addTag(TagId tag) {
            this.tags = this.tags.add(tag);
    }

    public void removeTag(TagId tag) {
        this.tags = this.tags.remove(tag);
    }

    public boolean hasTag(TagId tag) {
        return this.tags.contains(tag);
    }


    public CourseUUID uuid() { return uuid; }
    public Long id() { return id; }
    public CourseTitle title() { return title; }
    public CourseDescription description() { return description; }
    public CourseDifficulty difficulty() { return difficulty; }
    public CourseSections sections() { return sections; }
    public String thumbnailUrl() { return thumbnailUrl; }
    public InstructorUUID instructorUUID() { return instructorUUID; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public CourseDuration totalDuration() {
        return sections.totalDuration();
    }

    @Override
    public CourseUUID getId() {
        return uuid;
    }
}
