package com.lxp.content.course.domain.model.collection;


import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.id.SectionUUID;
import com.lxp.content.course.domain.model.vo.duration.CourseDuration;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record CourseSections(List<Section> values){

    public CourseSections {
        Objects.requireNonNull(values);
        List<Section> sorted = new ArrayList<>(values);
        sorted.sort(Comparator.comparingInt(Section::order));
        values = List.copyOf(sorted);
    }

    public static CourseSections empty() {
        return new CourseSections(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public static CourseSections of(List<Section> sections) {
        return new CourseSections(sections);
    }

    public CourseSections addSection(SectionUUID uuid, String title) {
        validateDuplicateUUID(uuid);

        List<Section> newList = new ArrayList<>(values);
        newList.add(Section.create(title, uuid, values.size() + 1));

        return new CourseSections(newList);
    }

    public CourseSections removeSection(SectionUUID uuid) {

        List<Section> newList = values.stream()
                .filter(sec -> !sec.uuid().equals(uuid))
                .toList();

        return reorderAll(newList);
    }

    public CourseSections renameSection(SectionUUID uuid, String title) {
        return map(uuid, sec -> sec.rename(title));
    }

    public CourseSections reorderSection(SectionUUID uuid, int newOrder) {
        Section target = values.stream()
                .filter(sec -> sec.uuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> CourseException.sectionNotFound(uuid.value()));

        List<Section> newList = new ArrayList<>(values);
        newList.remove(target);
        target.changeOrder(newOrder);
        newList.add(Math.min(newOrder - 1, newList.size()), target);

        return reorderAll(newList);
    }


    public CourseSections addLecture(SectionUUID sectionUUID, LectureUUID lectureUUID, String title,
                                     LectureDuration duration,
                                     String url) {
        return map(sectionUUID, section -> section.addLecture(lectureUUID, title, duration, url));
    }

    public CourseSections removeLecture(SectionUUID sectionUUID, LectureUUID lectureUUID) {
        return map(sectionUUID, section -> section.removeLecture(lectureUUID));
    }

    public CourseSections renameLecture(SectionUUID sectionUUID, LectureUUID lectureUUID, String title) {
        return map(sectionUUID, section -> section.renameLecture(lectureUUID, title));
    }

    public CourseSections changeLectureVideoUrl(SectionUUID sectionUUID, LectureUUID lectureUUID, String url) {
        return map(sectionUUID, section -> section.changeLectureVideoUrl(lectureUUID, url));
    }

    private CourseSections map(SectionUUID uuid, Function<Section, Section> fn) {
        boolean found = false;
        List<Section> newList = new ArrayList<>();

        for (Section s : values) {
            if (s.uuid().equals(uuid)) {
                newList.add(fn.apply(s));
                found = true;
            } else {
                newList.add(s);
            }
        }

        if (!found)
            throw CourseException.sectionNotFound(uuid.value());

        return new CourseSections(newList);
    }

    private CourseSections reorderAll(List<Section> list) {
        int order = 1;
        for (Section s : list) {
            s.changeOrder(order++);
        }
        return new CourseSections(list);
    }

    private void validateDuplicateUUID(SectionUUID uuid) {
        if(uuid == null) return;
        if (values.stream().anyMatch(s -> s.uuid().equals(uuid)))
            throw CourseException.sectionDuplicateUuid(uuid.value());
    }

    public CourseDuration totalDuration() {
        long seconds = values.stream()
                .mapToLong(s -> s.totalDuration().seconds())
                .sum();
        return seconds == 0 ? new CourseDuration(1) : new CourseDuration(seconds);
    }

    public int size() {
        return values.size();
    }
}
