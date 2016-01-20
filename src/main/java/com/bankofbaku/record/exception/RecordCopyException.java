package com.bankofbaku.record.exception;

public class RecordCopyException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordCopyException() {

    }

    public RecordCopyException(String message) {
        super(message);
    }

    public RecordCopyException(Throwable cause) {
        super(cause);
    }

    public RecordCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordCopyException(String message, Throwable cause,
                               boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
