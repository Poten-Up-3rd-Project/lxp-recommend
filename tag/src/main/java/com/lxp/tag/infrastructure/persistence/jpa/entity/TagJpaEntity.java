package com.lxp.tag.infrastructure.persistence.jpa.entity;

import com.lxp.tag.application.port.query.TagResult;
import com.lxp.tag.infrastructure.persistence.jpa.enums.TagState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String color;

    @Column(nullable = false, length = 30)
    private String variant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TagState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", nullable = false)
    private TagCategoryJpaEntity category;

    public static TagResult toResult(TagJpaEntity entity) {
        return new TagResult(
                entity.id,
                entity.name,
                entity.state.toString(),
                entity.color,
                entity.variant
        );
    }
}
