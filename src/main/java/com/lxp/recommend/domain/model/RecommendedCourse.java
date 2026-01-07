package com.lxp.recommend.domain.model;

import com.lxp.recommend.domain.model.ids.CourseId;

public class RecommendedCourse {

    private final CourseId courseId;
    private final double score;
    private final int rank;

    public RecommendedCourse(CourseId courseId, double score, int rank) {
        if (courseId == null) throw new IllegalArgumentException("courseId is required");
        this.courseId = courseId;
        this.score = score;
        this.rank = rank;
    }

    public CourseId getCourseId() { return courseId; }
    public double getScore() { return score; }
    public int getRank() { return rank; }
}
