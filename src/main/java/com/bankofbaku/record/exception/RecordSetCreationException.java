package com.bankofbaku.record.exception;

public class RecordSetCreationException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordSetCreationException() {

    }

    public RecordSetCreationException(String message) {
        super(message);
    }

    public RecordSetCreationException(Throwable cause) {
        super(cause);
    }

    public RecordSetCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordSetCreationException(String message, Throwable cause,
                                      boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
