package org.mcezario.diff.domains.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public final class ExceptionId implements Serializable {

    private static final long serialVersionUID = -2167231257089167282L;

    private static final String MESSAGE_FORMAT = "[%s] - %s";

    private final String code;
    private final String message;
    private final int httpStatusCode;

    private ExceptionId(final String code, final String message, final int httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public static ExceptionId create(final String code, final String message) {
        return new ExceptionId(code, message, HttpStatus.BAD_REQUEST.value());
    }

    public static ExceptionId internalError() {
        return new ExceptionId(INTERNAL_SERVER_ERROR.name(), INTERNAL_SERVER_ERROR.getReasonPhrase(),
                INTERNAL_SERVER_ERROR.value());
    }

    @Override
    public String toString() {
        return String.format(MESSAGE_FORMAT, code, message);
    }

    public ExceptionId withHttpStatus(final HttpStatus httpStatus) {
        return new ExceptionId(code, message, httpStatus.value());
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int httpStatusCode() {
        return httpStatusCode;
    }

}
