package com.bankofbaku.exception;

public class PasswordCheckException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public PasswordCheckException() {

    }

    public PasswordCheckException(String message) {
        super(message);
    }

    public PasswordCheckException(Throwable cause) {
        super(cause);
    }

    public PasswordCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordCheckException(String message, Throwable cause,
                                  boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
