package com.lxp.content.course.domain.model;

import com.lxp.common.domain.model.BaseEntity;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;

import java.util.Objects;

public class Lecture extends BaseEntity<LectureUUID> {
    private final LectureUUID uuid;
    private final Long id;
    private String title;
    private LectureDuration duration;
    private int order;
    private String videoUrl;

    private Lecture(Long id, LectureUUID uuid, String title,
                    LectureDuration duration, int order, String videoUrl) {
        this.id = id;
        this.uuid = Objects.requireNonNull(uuid);
        this.title = Objects.requireNonNull(title);
        this.duration = Objects.requireNonNull(duration);
        this.order = order;
        this.videoUrl = Objects.requireNonNull(videoUrl);
    }

    public static Lecture create(String title, LectureUUID uuid, LectureDuration duration,
                                 int order, String videoUrl) {
        return new Lecture(
                null,
                uuid,
                title,
                duration,
                order,
                videoUrl
        );
    }

    public static Lecture reconstruct(
            LectureUUID uuid,
            Long id,
            String title,
            LectureDuration duration,
            int order,
            String videoUrl
    ) {
        return new Lecture(
                id,
                uuid,
                title,
                duration,
                order,
                videoUrl
        );
    }

    public void changeTitle(String title) {
        this.title = Objects.requireNonNull(title,"lecture title cannot be null");
    }

    public void changeVideoUrl(String url) {
        this.videoUrl = Objects.requireNonNull(url,"lecture videoUrl cannot be null");
    }

    public void changeOrder(int order) {
        this.order = order;
    }


    public LectureUUID uuid() { return uuid; }
    public Long id() { return id; }
    public String title() { return title; }
    public LectureDuration duration() { return duration; }
    public int order() { return order; }
    public String videoUrl() { return videoUrl; }

    @Override
    public LectureUUID getId() {
        return uuid;
    }
}