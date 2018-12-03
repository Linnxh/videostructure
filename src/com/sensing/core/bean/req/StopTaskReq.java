package com.sensing.core.bean.req;

import java.io.Serializable;

public class StopTaskReq extends BaseRequest {
    private String taskUuid;

    private Integer state;

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
