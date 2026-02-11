package com.lxp.recommend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C002", "Bad request"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C003", "Invalid input value"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "User already exists"),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "Course not found"),
    COURSE_ALREADY_EXISTS(HttpStatus.CONFLICT, "CO002", "Course already exists"),

    // Recommendation
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "Recommendation not found"),

    // Storage
    STORAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "Storage upload failed"),
    STORAGE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "Storage download failed"),

    // Batch
    BATCH_JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "B001", "Batch job failed"),
    PARQUET_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "B002", "Parquet write failed"),

    // Engine
    ENGINE_CALL_FAILED(HttpStatus.BAD_GATEWAY, "E001", "Engine call failed");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
