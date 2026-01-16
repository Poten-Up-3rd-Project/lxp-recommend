package com.lxp.recommend.infrastructure.constants;

/**
 * Passport 관련 상수
 */
public interface PassportConstants {

    // 헤더
    String PASSPORT_HEADER_NAME = "X-Passport";

    // JWT Claims
    String PASSPORT_USER_ID = "uid";
    String PASSPORT_ROLE = "rol";
    String PASSPORT_TRACE_ID = "tid";
}
