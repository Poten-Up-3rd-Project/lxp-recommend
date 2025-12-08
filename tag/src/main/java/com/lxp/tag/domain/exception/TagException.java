package com.lxp.tag.domain.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.domain.exception.ErrorCode;

public class TagException extends DomainException {
    public TagException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected TagException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected TagException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
