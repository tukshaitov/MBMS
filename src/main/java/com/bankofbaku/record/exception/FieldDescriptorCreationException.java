package com.bankofbaku.record.exception;

public class FieldDescriptorCreationException extends RuntimeException {

    private static final long serialVersionUID = 1817946338331371915L;

    public FieldDescriptorCreationException() {

    }

    public FieldDescriptorCreationException(String message) {
        super(message);
    }

    public FieldDescriptorCreationException(Throwable cause) {
        super(cause);
    }

    public FieldDescriptorCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldDescriptorCreationException(String message, Throwable cause,
                                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
