package com.bankofbaku.exception;

public class TaskInstanceNotCloseableException extends Exception {

    private static final long serialVersionUID = 1817946338331371915L;

    public TaskInstanceNotCloseableException() {

    }

    public TaskInstanceNotCloseableException(String message) {
        super(message);
    }

    public TaskInstanceNotCloseableException(Throwable cause) {
        super(cause);
    }

    public TaskInstanceNotCloseableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskInstanceNotCloseableException(String message, Throwable cause,
                                             boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
