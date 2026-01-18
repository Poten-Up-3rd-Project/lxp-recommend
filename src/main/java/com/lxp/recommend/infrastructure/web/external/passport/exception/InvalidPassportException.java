package com.lxp.recommend.infrastructure.web.external.passport.exception;

/**
 * Passport 검증 실패 시 발생하는 예외
 */
public class InvalidPassportException extends RuntimeException {

    public InvalidPassportException(String message) {
        super(message);
    }

    public InvalidPassportException(String message, Throwable cause) {
        super(message, cause);
    }
}
