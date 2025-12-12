package com.lxp.common.constants;

/**
 * HTTP Cookie 이름 및 관련 상수 정의
 */
public final class CookieConstants {

    private CookieConstants() {
    } // 인스턴스화 방지

    // Access Token Cookie 이름
    public static final String ACCESS_TOKEN_NAME = "access_token";

    // HTTP Only 속성
    public static final boolean HTTP_ONLY = true;

    // Cookie Path
    public static final String DEFAULT_PATH = "/";
}
