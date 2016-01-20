package com.bankofbaku.record.exception;

public class RecordCloneException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordCloneException() {

    }

    public RecordCloneException(String message) {
        super(message);
    }

    public RecordCloneException(Throwable cause) {
        super(cause);
    }

    public RecordCloneException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordCloneException(String message, Throwable cause,
                                boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
