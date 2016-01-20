package com.bankofbaku.record.exception;

public class RecordFillException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordFillException() {

    }

    public RecordFillException(String message) {
        super(message);
    }

    public RecordFillException(Throwable cause) {
        super(cause);
    }

    public RecordFillException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordFillException(String message, Throwable cause,
                               boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
