package com.lxp.content.course.domain.model.collection;

import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.id.TagId;

import java.util.ArrayList;
import java.util.List;


public record CourseTags(List<TagId> values) {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 5;

    public CourseTags {
        values = List.copyOf(values == null ? List.of() : values);

        if (values.isEmpty() || values.size() > MAX_SIZE) {
            throw CourseException.tagsValidationError();
        }
    }

    public static CourseTags of(List<TagId> tags) {
        return new CourseTags(tags);
    }

    public CourseTags add(TagId tag) {
        List<TagId> newList = new ArrayList<>(values);
        newList.add(tag);
        return new CourseTags(newList);
    }

    public CourseTags remove(TagId tag) {
        List<TagId> newList = new ArrayList<>(values);
        newList.remove(tag);
        return new CourseTags(newList);
    }

    public boolean contains(TagId tagId) {
        return values.contains(tagId);
    }
}
