package com.lxp.content.course.domain.model.vo.duration;

import java.util.concurrent.ThreadLocalRandom;

public record LectureDuration(long seconds)  {
    private static final long MAX_SECONDS = 20 * 60;
    public LectureDuration {
        if (seconds <= 0 || seconds > MAX_SECONDS) {
            throw new IllegalArgumentException("lecture duration must be positive");
        }
    }
    public static LectureDuration randomUnder20Minutes() {
        long randomSec = ThreadLocalRandom.current().nextLong(1, MAX_SECONDS);
        return new LectureDuration(randomSec);
    }
}