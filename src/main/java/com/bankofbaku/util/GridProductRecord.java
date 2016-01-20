package com.bankofbaku.util;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;

@JsonDeserialize(using = GridProductRecordDeserializer.class)
public class GridProductRecord implements java.io.Serializable {
    private static final long serialVersionUID = 7625295026454607175L;
    private Integer id;
    private Integer parentId;
    private String name;
    private Date activated;

    public GridProductRecord() {

    }

    public GridProductRecord(Integer id, Integer parentId, String name, Date activated) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.activated = activated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getActivated() {
        return activated;
    }

    public void setActivated(Date activated) {
        this.activated = activated;
    }

}