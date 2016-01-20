package com.bankofbaku.record.exception;

public class RecordSetFactoryException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordSetFactoryException() {

    }

    public RecordSetFactoryException(String message) {
        super(message);
    }

    public RecordSetFactoryException(Throwable cause) {
        super(cause);
    }

    public RecordSetFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordSetFactoryException(String message, Throwable cause,
                                     boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
