package com.lxp.tag.domain.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum TagErrorCode implements ErrorCode {
    INVALID_CHANGE_CATEGORY("TAG_001", "Invalid tag can not change category", "BAD_REQUEST"),
    ;

    private final String code;
    private final String message;
    private final String group;

    TagErrorCode(String code, String message, String group) {
        this.code = code;
        this.message = message;
        this.group = group;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getGroup() {
        return this.group;
    }
}
