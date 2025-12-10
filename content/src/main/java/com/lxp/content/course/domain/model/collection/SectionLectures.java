package com.lxp.content.course.domain.model.collection;

import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.id.LectureUUID;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.model.vo.duration.SectionDuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record SectionLectures(List<Lecture> values)  {

    public SectionLectures {
        List<Lecture> sorted = new ArrayList<>(values);
        sorted.sort(Comparator.comparingInt(Lecture::order));
        values = List.copyOf(sorted);
    }

    public static SectionLectures empty() {
        return new SectionLectures(List.of());
    }

    public static SectionLectures of(List<Lecture> lectures) {
        return new SectionLectures(lectures);
    }

    public Optional<Lecture> findById(LectureUUID uuid) {
        return values.stream()
                .filter(l -> l.uuid().equals(uuid))
                .findFirst();
    }

    public int size() {
        return values.size();
    }

    public SectionDuration totalDuration() {
        long sec = values.stream()
                .mapToLong(l -> l.duration().seconds())
                .sum();

        return sec == 0 ? new SectionDuration(1) : new SectionDuration(sec);
    }


    public SectionLectures addLecture(
            LectureUUID uuid, String title, LectureDuration duration, String videoUrl
    ) {
        validateDuplicate(uuid);

        List<Lecture> newList = new ArrayList<>(values);

        newList.add(Lecture.create(title, uuid, duration, newList.size() + 1, videoUrl));

        return new SectionLectures(newList);
    }


    public SectionLectures removeLecture(LectureUUID uuid) {
        List<Lecture> newList = values.stream()
                .filter(l -> !l.uuid().equals(uuid))
                .toList();

        return reorderAll(newList);
    }


    public SectionLectures renameLecture(LectureUUID uuid, String newTitle) {
        return map(uuid, l -> {
            l.changeTitle(newTitle);
            return l;
        });
    }


    public SectionLectures changeLectureVideoUrl(LectureUUID uuid, String url) {
        return map(uuid, l -> {
            l.changeVideoUrl(url);
            return l;
        });
    }


    private SectionLectures map(LectureUUID uuid, Function<Lecture, Lecture> fn) {
        List<Lecture> newList = new ArrayList<>();
        boolean found = false;

        for (Lecture lec : values) {
            if (lec.uuid().equals(uuid)) {
                found = true;
                newList.add(fn.apply(lec));
            } else {
                newList.add(lec);
            }
        }

        if (!found) throw CourseException.lectureNotFound(uuid.value());

        return new SectionLectures(newList);
    }

    private SectionLectures reorderAll(List<Lecture> list) {
        int order = 1;
        for (Lecture l : list) {
            l.changeOrder(order++);
        }
        return new SectionLectures(list);
    }

    private void validateDuplicate(LectureUUID uuid) {
        if (values.stream().anyMatch(l -> l.uuid().equals(uuid)))
            throw CourseException.lectureDuplicateUuid(uuid.value());
    }

}
