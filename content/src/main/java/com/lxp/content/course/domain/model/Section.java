package com.lxp.content.course.domain.model;

import com.lxp.common.domain.model.BaseEntity;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.id.SectionUUID;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.model.vo.duration.SectionDuration;

import java.util.Objects;

public class Section extends BaseEntity<SectionUUID> {
    private final Long id;
    private final SectionUUID uuid;
    private String title;
    private int order;
    private SectionLectures lectures;

    private Section(Long id, SectionUUID uuid, String title, int order, SectionLectures lectures) {
        this.id = id;
        this.uuid = Objects.requireNonNull(uuid);
        this.title = Objects.requireNonNull(title);
        this.order = order;
        this.lectures = Objects.requireNonNull(lectures);
    }

    public static Section create(String title, SectionUUID uuid, int order) {
        return create(title, uuid, order, SectionLectures.empty());
    }

    public static Section create(String title, SectionUUID uuid, int order, SectionLectures lectures) {
        return new Section(
                null,
                uuid,
                title,
                order,
                lectures
        );
    }

    public static Section reconstruct(
            Long id,
            SectionUUID uuid,
            String title,
            int order,
            SectionLectures lectures
    ) {
        return new Section(id, uuid, title, order, lectures);
    }

    public Section rename(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        return this;
    }

    public Section addLecture(LectureUUID uuid, String title, LectureDuration duration, String url) {
        this.lectures = lectures.addLecture(uuid, title, duration, url);
        return this;
    }

    public Section removeLecture(LectureUUID uuid) {
        this.lectures = lectures.removeLecture(uuid);
        return this;
    }

    public Section renameLecture(LectureUUID uuid, String title) {
        this.lectures = lectures.renameLecture(uuid, title);
        return this;
    }


    public Section changeLectureVideoUrl(LectureUUID uuid, String url) {
        this.lectures = lectures.changeLectureVideoUrl(uuid, url);
        return this;
    }


    public void changeOrder(int newOrder) {
        this.order = newOrder;
    }

    // getter
    public SectionUUID uuid() { return uuid; }
    public Long id() { return id; }
    public String title() { return title; }
    public int order() { return order; }
    public SectionLectures lectures() { return lectures; }

    public SectionDuration totalDuration() {
        return lectures.totalDuration();
    }

    @Override
    public SectionUUID getId() {
        return uuid;
    }
}