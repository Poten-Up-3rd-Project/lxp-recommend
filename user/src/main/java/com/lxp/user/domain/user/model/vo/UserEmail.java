package com.lxp.user.domain.user.model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public record UserEmail(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public UserEmail {
        if (isNull(value) || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(value);
        }
    }

    public static UserEmail of(String value) {
        return new UserEmail(value);
    }

}
