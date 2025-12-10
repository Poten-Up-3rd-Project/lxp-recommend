package com.lxp.content.course.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "lecture")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureJpaEntity extends BaseJpaEntity {
    @Column(nullable = false, unique = true)
    @Getter
    private String uuid;

    @Column(nullable = false)
    @Getter
    private String title;

    @Getter
    private Long durationSeconds;

    @Getter
    @Column(name = "sort_order")
    private int order;

    @Getter
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionJpaEntity section;

    @Builder
    public LectureJpaEntity(String uuid, String title, Long duration, int order, String videoUrl) {
        this.uuid = uuid;
        this.title = title;
        this.durationSeconds = duration;
        this.order = order;
        this.videoUrl = videoUrl;
    }

    public void assignSection(SectionJpaEntity section) {
        this.section = section;
    }

}
