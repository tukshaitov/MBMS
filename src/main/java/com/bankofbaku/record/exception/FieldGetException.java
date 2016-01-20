package com.bankofbaku.record.exception;

public class FieldGetException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public FieldGetException() {

    }

    public FieldGetException(String message) {
        super(message);
    }

    public FieldGetException(Throwable cause) {
        super(cause);
    }

    public FieldGetException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldGetException(String message, Throwable cause,
                             boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
