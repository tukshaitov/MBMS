package com.bankofbaku.record.exception;

public class CrudException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public CrudException() {

    }

    public CrudException(String message) {
        super(message);
    }

    public CrudException(Throwable cause) {
        super(cause);
    }

    public CrudException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrudException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
