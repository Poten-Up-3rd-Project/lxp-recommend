package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provided.dto.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provided.usecase.CourseCreateUseCase;
import org.springframework.stereotype.Service;

@Service
public class CourseCreateService implements CourseCreateUseCase {
    @Override
    public Long execute(CourseCreateCommand input) {
        return 0L;
    }
}
