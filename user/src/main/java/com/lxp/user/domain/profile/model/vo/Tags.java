package com.lxp.user.domain.profile.model.vo;

import com.lxp.user.domain.profile.exception.TagSizeConstraintViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Tags(List<Long> values) {

    private static final int MAX_SIZE = 5;
    private static final int MIN_SIZE = 3;

    public Tags {
        if (Objects.isNull(values) || values.isEmpty()) {
            values = Collections.emptyList();
        }

        if (values.size() < MIN_SIZE || values.size() > MAX_SIZE) {
            throw new TagSizeConstraintViolationException(MIN_SIZE, MAX_SIZE);
        }
        values = List.copyOf(values);
    }

    public Tags withTags(List<Long> newTags) {
        return new Tags(newTags);
    }

}
