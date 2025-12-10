package com.lxp.content.course.application.mapper;

import com.lxp.content.course.application.port.provided.dto.result.CourseInfoResult;
import com.lxp.content.course.application.port.provided.dto.result.CourseResult;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.id.TagId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseResultMapper {

    public CourseResult toResult(Course course) {
        return new CourseResult(
                course.uuid().value(),
                course.id(),
                course.instructorUUID().value(),
                course.title().value(),
                course.thumbnailUrl(),
                course.description().value(),
                course.difficulty(),
                toTagIds(course.tags())
        );
    }


    public CourseInfoResult toInfoResult(Course course) {
        return new CourseInfoResult(
                course.uuid().value(),
                course.id(),
                course.instructorUUID().value(),
                course.title().value(),
                course.thumbnailUrl(),
                course.description().value(),
                course.totalDuration().toMinutes(),
                course.difficulty(),
                toSectionResults(course.sections()),
                toTagIds(course.tags())
        );
    }

    private List<CourseInfoResult.SectionInfoResult> toSectionResults(CourseSections sections) {
        return sections.values().stream()
                .map(this::toSectionResult)
                .toList();
    }

    private CourseInfoResult.SectionInfoResult toSectionResult(Section section) {
        return new CourseInfoResult.SectionInfoResult(
                section.uuid().value(),
                section.id(),
                section.title(),
                section.order(),
                toLectureResults(section.lectures())
        );
    }

    private List<CourseInfoResult.SectionInfoResult.LectureInfoResult> toLectureResults(SectionLectures lectures) {
        return lectures.values().stream()
                .map(this::toLectureResult)
                .toList();
    }

    private CourseInfoResult.SectionInfoResult.LectureInfoResult toLectureResult(Lecture lecture) {
        return new CourseInfoResult.SectionInfoResult.LectureInfoResult(
                lecture.uuid().value(),
                lecture.id(),
                lecture.title(),
                lecture.videoUrl(),
                lecture.duration().seconds(),
                lecture.order()
        );
    }

    private List<Long> toTagIds(CourseTags tags) {
        return tags.values().stream()
                .map(TagId::value)
                .toList();
    }
}
