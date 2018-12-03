package com.sensing.core.bean.job.req;

import com.sensing.core.bean.req.BaseRequest;
import com.sensing.core.utils.Pager;

import java.io.Serializable;

public class JobListCountReq extends BaseRequest {

    private Integer querytype;
    private Integer isRatifyList;

    public Integer getQuerytype() {
        return querytype;
    }

    public void setQuerytype(Integer querytype) {
        this.querytype = querytype;
    }

    public Integer getIsRatifyList() {
        return isRatifyList;
    }

    public void setIsRatifyList(Integer isRatifyList) {
        this.isRatifyList = isRatifyList;
    }
}
