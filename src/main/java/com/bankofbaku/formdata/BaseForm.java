package com.bankofbaku.formdata;

import com.bankofbaku.record.Record;
import com.bankofbaku.util.SimpleResponse;

import java.util.List;
import java.util.Map;

public class BaseForm extends SimpleResponse {
    private static final long serialVersionUID = 1L;
    private boolean isDirty;
    private boolean isSelected;

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void save() {
    }

    public void refresh() {
        this.isDirty = false;
    }

    public void init() {
    }

    public void clear() {
        this.isDirty = false;
    }

    public Map<String, List<Record>> getRecordsFromVersion(Map<String, Integer> versions) {
        return null;
    }

    public void onComit() {
    }
}
