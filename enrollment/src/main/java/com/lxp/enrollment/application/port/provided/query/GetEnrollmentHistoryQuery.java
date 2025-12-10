package com.lxp.enrollment.application.port.provided.query;

public record GetEnrollmentHistoryQuery(
    String userId,
    String state, // ENROLLED, COMPLETED, CANCELLED
    int page,
    int size,
    String sort // LATEST, OLDEST
) {

}
