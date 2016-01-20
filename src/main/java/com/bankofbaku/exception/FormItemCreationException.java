package com.bankofbaku.exception;

public class FormItemCreationException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public FormItemCreationException() {

    }

    public FormItemCreationException(String message) {
        super(message);
    }

    public FormItemCreationException(Throwable cause) {
        super(cause);
    }

    public FormItemCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormItemCreationException(String message, Throwable cause,
                                     boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
