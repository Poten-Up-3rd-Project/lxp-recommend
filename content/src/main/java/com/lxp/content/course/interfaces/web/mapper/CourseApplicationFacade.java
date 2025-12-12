package com.lxp.content.course.interfaces.web.mapper;

import com.lxp.api.content.course.port.usecase.dto.command.CourseCreateCommand;
import com.lxp.api.content.course.port.usecase.dto.result.CourseDetailView;
import com.lxp.common.application.cqrs.CommandBus;
import com.lxp.content.course.interfaces.web.dto.response.CourseDetailResponse;
import com.lxp.content.course.interfaces.web.dto.reuqest.create.CourseCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseApplicationFacade {
    private final CourseWebMapper mapper;
    private final CommandBus commandBus;

    public CourseDetailResponse createCourse(CourseCreateRequest request) {
        CourseCreateCommand command = mapper.toCreateCommand(request);
        CourseDetailView result = commandBus.dispatchWithResult(command);
        return mapper.toDetailResponse(result);
    }
}
