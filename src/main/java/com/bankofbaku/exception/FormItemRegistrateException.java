package com.bankofbaku.exception;

public class FormItemRegistrateException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public FormItemRegistrateException() {

    }

    public FormItemRegistrateException(String message) {
        super(message);
    }

    public FormItemRegistrateException(Throwable cause) {
        super(cause);
    }

    public FormItemRegistrateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormItemRegistrateException(String message, Throwable cause,
                                       boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
