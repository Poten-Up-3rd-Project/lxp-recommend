package com.lxp.content.course.application.port.provided.usecase;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.course.application.port.provided.dto.command.CourseCreateCommand;

public interface CourseCreateUseCase extends UseCase<CourseCreateCommand,Long> {

}
