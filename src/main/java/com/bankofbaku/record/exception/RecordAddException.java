package com.bankofbaku.record.exception;

public class RecordAddException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordAddException() {

    }

    public RecordAddException(String message) {
        super(message);
    }

    public RecordAddException(Throwable cause) {
        super(cause);
    }

    public RecordAddException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordAddException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
