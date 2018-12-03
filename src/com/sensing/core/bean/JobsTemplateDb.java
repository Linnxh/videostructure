package com.sensing.core.bean;

import java.io.Serializable;


public class JobsTemplateDb implements Serializable {
    private String uuid;
    private String job_uuid;
    private Integer templatedb_id;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getJob_uuid() {
        return job_uuid;
    }

    public void setJob_uuid(String job_uuid) {
        this.job_uuid = job_uuid;
    }

    public Integer getTemplatedb_id() {
        return templatedb_id;
    }

    public void setTemplatedb_id(Integer templatedb_id) {
        this.templatedb_id = templatedb_id;
    }
}
