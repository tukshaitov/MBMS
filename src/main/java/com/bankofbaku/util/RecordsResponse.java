package com.bankofbaku.util;

import com.bankofbaku.record.Record;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordsResponse implements IResponse {
    private static final long serialVersionUID = 5469588786889027803L;
    private String message = "";
    private Map<String, Object> metaData = new HashMap<String, Object>();
    private List<Record> records;
    private Boolean success;

    public RecordsResponse(Integer version, boolean isDirty, List<Record> records) {
        this(version, isDirty, null, records);
    }

    public RecordsResponse(Integer version, boolean isDirty, Integer selected, List<Record> records) {
        this.message = "";
        this.records = records;
        this.success = true;
        metaData.put("isDirty", isDirty);
        metaData.put("version", version);
        metaData.put("selected", selected);
    }

    public RecordsResponse(Integer version, boolean isDirty, Record... records) {
        this(version, isDirty, null, Arrays.asList(records));
    }

    public RecordsResponse(Integer version, boolean isDirty, Integer selected, Record... records) {
        this(version, isDirty, selected, Arrays.asList(records));
    }

    public String getMessage() {
        return message;
    }

    public List<Record> getRecords() {
        return records;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }
}
