package com.lxp.content.course.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseVersionedJpaEntity;
import com.lxp.content.course.domain.model.enums.CourseDifficulty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "course",  indexes = {
        @Index(name = "idx_course_uuid", columnList = "uuid")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseJpaEntity extends BaseVersionedJpaEntity {
    @Getter
    @Column(nullable = false, unique = true)
    private String uuid;

    @Getter
    @Column(name = "instructor_id", nullable = false)
    private String instructorUUID;

    @Getter
    @Column(nullable = false)
    private String title;

    @Getter
    private String description;

    @Getter
    private String thumbnailUrl;

    @Getter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<SectionJpaEntity> sections = new ArrayList<>();

    @Getter
    @Enumerated(EnumType.STRING)
    private CourseDifficulty difficulty;

    @Getter
    @ElementCollection
    @CollectionTable(
            name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            indexes = @Index(name = "idx_course_tags_tag_id", columnList = "tag_id")
    )
    @Column(name = "tag_id")
    @OrderColumn(name = "tag_order")
    private List<Long> tags = new ArrayList<>();

    @Builder
    public CourseJpaEntity(String uuid, String instructorUUID, String title,
                           String description, String thumbnailUrl,
                           CourseDifficulty difficulty, List<Long> tags) {
        this.uuid = uuid;
        this.instructorUUID = instructorUUID;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.difficulty = difficulty;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public void addSection(SectionJpaEntity section) {
        this.sections.add(section);
        section.assignCourse(this);
    }
}
