package com.lxp.recommend.application.port.in;

import com.lxp.recommend.application.dto.CourseSyncCommand;

public interface CourseSyncUseCase {

    void createCourse(CourseSyncCommand command);

    void deleteCourse(String eventId, String courseId);
}
