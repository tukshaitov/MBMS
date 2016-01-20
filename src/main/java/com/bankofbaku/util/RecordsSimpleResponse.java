package com.bankofbaku.util;

public class RecordsSimpleResponse implements IResponse {
    private static final long serialVersionUID = 5469588786889027803L;
    private String message = "";
    private Boolean success = true;

    public RecordsSimpleResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public RecordsSimpleResponse(Boolean success) {
        this.message = "";
        this.success = success;
    }

    public RecordsSimpleResponse() {
        this.message = "";
        this.success = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
