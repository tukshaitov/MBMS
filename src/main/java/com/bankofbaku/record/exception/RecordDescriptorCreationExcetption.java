package com.bankofbaku.record.exception;

public class RecordDescriptorCreationExcetption extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public RecordDescriptorCreationExcetption() {

    }

    public RecordDescriptorCreationExcetption(String message) {
        super(message);
    }

    public RecordDescriptorCreationExcetption(Throwable cause) {
        super(cause);
    }

    public RecordDescriptorCreationExcetption(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordDescriptorCreationExcetption(String message, Throwable cause,
                                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
