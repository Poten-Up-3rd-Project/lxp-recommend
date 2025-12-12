package com.lxp.content.course.interfaces.web.dto.response;

import com.lxp.common.response.EnumResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class CourseResponse {
    String id;
    InstructorResponse instructor;
    String title;
    String description;
    String thumbnailUrl;
    EnumResponse level;
    List<TagResponse> tags;

    public CourseResponse(String id, InstructorResponse instructor, String title, String description, String thumbnailUrl, EnumResponse level, List<TagResponse> tags) {
        this.id = id;
        this.instructor = instructor;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.level = level;
        this.tags = tags;
    }
}
