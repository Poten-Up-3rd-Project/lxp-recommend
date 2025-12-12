package com.lxp.content.course.application.port.required.dto;

public record InstructorResult(
    String userId,
    String name,
    String role,
    String status
) {
}
