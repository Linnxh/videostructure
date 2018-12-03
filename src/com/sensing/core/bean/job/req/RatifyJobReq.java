package com.sensing.core.bean.job.req;

import java.io.Serializable;

public class RatifyJobReq implements Serializable {

    private String jobUuid;
    private Integer ratifyResult;
    private String ratifyMemo;
    private String ratifyUser;

    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    public Integer getRatifyResult() {
        return ratifyResult;
    }

    public void setRatifyResult(Integer ratifyResult) {
        this.ratifyResult = ratifyResult;
    }

    public String getRatifyMemo() {
        return ratifyMemo;
    }

    public void setRatifyMemo(String ratifyMemo) {
        this.ratifyMemo = ratifyMemo;
    }

    public String getRatifyUser() {
        return ratifyUser;
    }

    public void setRatifyUser(String ratifyUser) {
        this.ratifyUser = ratifyUser;
    }
}
