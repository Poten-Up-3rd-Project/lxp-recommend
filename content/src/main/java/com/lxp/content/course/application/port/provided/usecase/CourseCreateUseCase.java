package com.lxp.content.course.application.port.provided.usecase;

import com.lxp.common.application.cqrs.CommandWithResultHandler;
import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.course.application.port.provided.dto.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provided.dto.result.CourseInfoResult;

public interface CourseCreateUseCase extends CommandWithResultHandler<CourseCreateCommand, CourseInfoResult> {

}

