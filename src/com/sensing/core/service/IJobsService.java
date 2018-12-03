package com.sensing.core.service;

import com.sensing.core.bean.Jobs;
import com.sensing.core.bean.job.req.JobListCountReq;
import com.sensing.core.bean.job.req.RatifyJobReq;
import com.sensing.core.bean.job.req.UpdateOperateReq;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;

/**
 * @author wenbo
 */
public interface IJobsService {

    public ResponseBean saveNewJobs(Jobs jobs);

    public ResponseBean updateCommon(Jobs jobs);

    public Jobs findJobsById(java.lang.String uuid);

    public void removeJobs(java.lang.String uuid);

    public ResponseBean updateOperate(UpdateOperateReq req);

    public Pager jobList(Pager pager);
    
    public int selectTemplatedbIdCount(Integer templatedbId);

    Pager ratifyList(Pager pager);

    ResponseBean ratifyJob(RatifyJobReq req);

    ResponseBean jobListCount(JobListCountReq req);

}