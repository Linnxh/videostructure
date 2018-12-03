package com.sensing.core.controller;

import javax.annotation.Resource;

import com.sensing.core.bean.job.req.JobListCountReq;
import com.sensing.core.bean.job.req.RatifyJobReq;
import com.sensing.core.bean.job.req.UpdateOperateReq;
import com.sensing.core.utils.*;
import com.sensing.core.utils.results.ResultUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sensing.core.bean.Jobs;
import com.sensing.core.service.IJobsService;

import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Pager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobsController extends BaseController {

    private static final Log log = LogFactory.getLog(JobsController.class);


    @Resource
    public IJobsService jobsService;

    /***
     * 布控管理-布控列表
     * @param p
     * {
     *   "pageRows": 10,
     *   "pageNo": 1,
     *   "pageFlag": "pageFlag",
     *   "f":{"name":"","state":"","level":"1","querytype":"0"}
     * }
     * type 0：所有列表    1:自己的申请列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/joblist")
    public ResponseBean jobList(@RequestBody JSONObject p) {
        Pager pager = JSONObject.toJavaObject(p, Pager.class);
        ResponseBean result = new ResponseBean();
        pager.getF().put("uuid", getUser().getUuid());
        pager = jobsService.jobList(pager);
        result.getMap().put("pager", pager);
        result.setError(0);
        result.setMessage("successful");

        return result;
    }

    /**
     * 布控：布控审批列表----不同布控状态的个数
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/joblistcount")
    public ResponseBean jobListCount(@RequestBody JobListCountReq req) {
        req.setUserUuid(getUser().getUuid());
        ResponseBean result = jobsService.jobListCount(req);
        return result;
    }


    /**
     * 布控管理-申请列表
     *
     * @param pager {
     *              "pageRows":10,
     *              "pageNo":1,
     *              "pageFlag":"pageFlag",
     *              "f":{
     *              "name":"",
     *              "level":"",
     *              "startTime":"",
     *              "endTime":"",
     *              "jobsType":"",
     *              "ratifyResult":"",
     *              "querytype":0  querytype  0：有审批权限看到的是所有列表    1：当前用户自己的申请列表
     *              }
     *              }
     * @return
     */
    @ResponseBody
    @RequestMapping("/ratifylist")
    public ResponseBean ratifyList(@RequestBody Pager pager) {
        ResponseBean result = new ResponseBean();
        pager.getF().put("uuid", getUser().getUuid());
        Pager pager1 = jobsService.ratifyList(pager);
        result.getMap().put("pager", pager1);
        result.setError(0);
        result.setMessage("successful");

        return result;
    }

    /**
     * 编辑页面的修改
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatecommon")
    public ResponseBean updateCommon(@RequestBody Jobs model) {
        ResponseBean result = new ResponseBean();
        if (model != null && model.getUuid() != null && !model.getUuid().equals("")) {
            String tips = checkValidJobs(model);
            if (StringUtils.isNotEmpty(tips)) {
                return new ResponseBean(-1, tips);
            }
            result = jobsService.updateCommon(model);
            result.getMap().put("model", model);
            result.setError(0);
            result.setMessage("successful");
        } else {
            result.setError(100);
            result.setMessage("business error");
        }

        return result;
    }

    /**
     * 新建布控
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResponseBean save(@RequestBody Jobs jobs) {
        String tips = checkValidJobs(jobs);
        if (StringUtils.isNotEmpty(tips)) {
            return new ResponseBean(-1, tips);
        }

        jobs.setCreateUser(getUser().getUuid());
        return jobsService.saveNewJobs(jobs);
    }

    private String checkValidJobs(@RequestBody Jobs jobs) {
        if (jobs == null || StringUtils.isEmptyOrNull(jobs.getName())) {
            return "布控名称为空";
        }
        if (jobs.getBeginDate() == null && StringUtils.isEmptyOrNull(jobs.getBeginTime())) {
            return "布控时段异常";
        }
        if (StringUtils.isEmptyOrNull(jobs.getRunWeek())) {
            return "布控日期异常";
        }
        if (CollectionUtils.isEmpty(jobs.getChannelUuIds())) {
            return "布控关联通道为空";
        }
        if (CollectionUtils.isEmpty(jobs.getTemplatedbIds()) && StringUtils.isEmptyOrNull(jobs.getPlateNo())) {
            return "布控模板为空";
        }

        return null;
    }

    @ResponseBody
    @RequestMapping("/delete")
    public ResponseBean delete(@RequestBody List<String> delUuids) {
        ResponseBean result = new ResponseBean();
        for (int i = 0; delUuids != null && i < delUuids.size(); i++) {
            jobsService.removeJobs(delUuids.get(i));
        }
        result.setError(0);
        result.setMessage("successful");

        return result;
    }

    /**
     * 布控：列表页的操作，暂停开启布控任务，取消订阅，删除，申请撤控
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateoperate")
    public ResponseBean updateOperate(@RequestBody UpdateOperateReq req) {
        if (req == null ) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        req.setUserUuid(getUser().getUuid());
        ResponseBean responseBean = jobsService.updateOperate(req);
        return responseBean;
    }

    /**
     * 审批操作
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("/ratifyjob")
    public ResponseBean ratifyJob(@RequestBody RatifyJobReq req) {
        if (req == null || req.getJobUuid() == null) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        req.setRatifyUser(getUser().getUuid());
        return jobsService.ratifyJob(req);
    }


}
