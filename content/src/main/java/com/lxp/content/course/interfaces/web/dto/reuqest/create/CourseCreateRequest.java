package com.lxp.content.course.interfaces.web.dto.reuqest.create;

import com.lxp.common.enums.Level;
import java.util.List;

//TODO("valid 체크 ")
public record CourseCreateRequest(
    String title,
    String description,
    String instructorId,
    String thumbnailUrl,
    Level level,
    List<Long> tags,
    List<SectionCreateRequest> sections
) {
}
