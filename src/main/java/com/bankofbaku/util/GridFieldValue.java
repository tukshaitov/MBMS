package com.bankofbaku.util;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(using = GridFieldValueDeserializer.class)
public class GridFieldValue implements java.io.Serializable {
    private static final long serialVersionUID = 7625295026454607175L;
    private Integer id;
    private String fieldName;
    private String value;

    public GridFieldValue() {

    }

    public GridFieldValue(Integer id, String fieldName, String value) {
        this.id = id;
        this.fieldName = fieldName;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GridFieldValue [id=" + id + ", fieldName=" + fieldName + ", value=" + value + "]";
    }
}