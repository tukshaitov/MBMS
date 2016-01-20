package com.bankofbaku.util;

import javax.persistence.Transient;

public class SimpleResponse implements IResponse {
    private static final long serialVersionUID = -8200509123518353105L;
    private Status status;
    private String message;

    public SimpleResponse() {
        this.status = status.ERROR;
        this.message = DefaultMessage;
    }

    public SimpleResponse(Status responseStatus, String message) {
        this.status = responseStatus;
        this.message = message;
    }

    @Transient
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status responseStatus) {
        this.status = responseStatus;
    }

    @Transient
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IResponse clone() {
        try {
            return (IResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
