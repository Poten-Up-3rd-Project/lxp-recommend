package com.lxp.content.course.interfaces.web.dto.response;

import com.lxp.common.response.EnumResponse;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Getter
public class CourseDetailResponse extends CourseResponse {
    List<SectionResponse> sections;
    long durationInHours;

    public CourseDetailResponse(String id,
                                InstructorResponse instructor,
                                String title,
                                String description,
                                String thumbnailUrl,
                                EnumResponse level,
                                List<TagResponse> tags,
                                List<SectionResponse> sections,
                                long durationInHours
    ) {
        super(id, instructor, title, description, thumbnailUrl, level, tags);
        this.sections = sections;
        this.durationInHours = durationInHours;
    }

}
