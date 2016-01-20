package com.bankofbaku.record.exception;

public class RecordCreationExcetption extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordCreationExcetption() {

    }

    public RecordCreationExcetption(String message) {
        super(message);
    }

    public RecordCreationExcetption(Throwable cause) {
        super(cause);
    }

    public RecordCreationExcetption(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordCreationExcetption(String message, Throwable cause,
                                    boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
