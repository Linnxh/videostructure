package com.sensing.core.service.impl;

import java.util.*;
import javax.annotation.Resource;

import com.sensing.core.alarm.DataInitService;
import com.sensing.core.bean.JobsChannel;
import com.sensing.core.bean.JobsTemplateDb;
import com.sensing.core.bean.SysSubscribe;
import com.sensing.core.bean.job.req.JobListCountReq;
import com.sensing.core.bean.job.req.RatifyJobReq;
import com.sensing.core.bean.job.req.UpdateOperateReq;
import com.sensing.core.bean.job.resp.JobListCountResp;
import com.sensing.core.bean.job.resp.JobListResp;
import com.sensing.core.bean.job.resp.JobRatifyListResp;
import com.sensing.core.dao.IJobsChannelDAO;
import com.sensing.core.dao.ISysSubscribeDAO;
import com.sensing.core.dao.ITemplateDAO;
import com.sensing.core.service.ITemplateObjMotorService;
import com.sensing.core.utils.*;
import com.sensing.core.utils.results.ResultUtils;
import org.springframework.stereotype.Service;
import com.sensing.core.bean.Jobs;
import com.sensing.core.service.IJobsService;
import com.sensing.core.dao.IJobsDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author wenbo
 */
@Service
public class JobsServiceImpl implements IJobsService {


    private static final Log log = LogFactory.getLog(IJobsService.class);

    @Resource
    public IJobsDAO jobsDAO;
    @Resource
    public IJobsChannelDAO jobsChannelDAO;
    @Resource
    public ISysSubscribeDAO sysSubscribeDAO;
    @Resource
    public TaskServiceImpl taskServiceImpl;
    @Resource
    public ITemplateObjMotorService templateObjMotorService;
    @Resource
    public DataInitService dataInitService;

    public JobsServiceImpl() {
        super();
    }

    @Override
    public ResponseBean saveNewJobs(Jobs jobs) {
        //名称去重
        int count = jobsDAO.getJobsByJobName(jobs.getName(), null);

        if (count > 0) {
            return new ResponseBean(-1, "已存在名称为" + jobs.getName() + "的布控任务");
        }
        String uuid = UuidUtil.getUuid();
        jobs.setUuid(uuid);

        //runWeek排序
        jobs.setRunWeek(taskServiceImpl.orderRunWeek(jobs.getRunWeek()));
        //布控
        jobsDAO.saveJobs(jobs);

        /****  新增通道和模板库   ****/
        addJobsChannel(jobs);
        addJobsTemplete(jobs);

        //如果是单目标库
        templateObjMotorService.saveObjMotorInSimpleDB(jobs.getPlateNo());
        return new ResponseBean();
    }

    @Override
    public ResponseBean updateCommon(Jobs jobs) {
        //名称去重
        int count = jobsDAO.getJobsByJobName(jobs.getName(), jobs.getUuid());
        if (count > 0) {
            return new ResponseBean(-1, "已存在名称为" + jobs.getName() + "的布控任务");
        }
        //布控
        jobsDAO.updateCommon(jobs);

        /****  移除通道和模板库   ****/
        jobsChannelDAO.removeJobsChannel(jobs.getUuid());
        jobsDAO.removeTempleteChannel(jobs.getUuid());

        /****  新增通道和模板库   ****/
        addJobsChannel(jobs);
        addJobsTemplete(jobs);

        dataInitService.init();

        return new ResponseBean();
    }

    /**
     * 添加布控关联的通道
     *
     * @param jobs
     */
    public void addJobsChannel(Jobs jobs) {
        if (CollectionUtils.isEmpty(jobs.getChannelUuIds())) {
            return;
        }
        List<JobsChannel> channelList = new ArrayList<>();
        JobsChannel c = null;
        List<String> channelUuIds = jobs.getChannelUuIds();
        for (String channelUuid : channelUuIds) {
            c = new JobsChannel();
            c.setUuid(UuidUtil.getUuid());
            c.setJobUuid(jobs.getUuid());
            c.setChannelUuid(channelUuid);
            c.setCreateUser(jobs.getCreateUser());
            channelList.add(c);
        }
        // TODO: 2018/11/15 lxh 保存关联通道
        jobsChannelDAO.saveJobsChannelBetch(channelList);

    }

    /**
     * 添加布控关联的通道
     *
     * @param jobs
     */
    public void addJobsTemplete(Jobs jobs) {
        if (CollectionUtils.isEmpty(jobs.getTemplatedbIds())) {
            return;
        }
        List<JobsTemplateDb> list = new ArrayList<>();
        JobsTemplateDb c = null;
        List<Integer> templatedbIds = jobs.getTemplatedbIds();
        for (Integer id : templatedbIds) {
            c = new JobsTemplateDb();
            c.setUuid(UuidUtil.getUuid());
            c.setJob_uuid(jobs.getUuid());
            c.setTemplatedb_id(id);
            list.add(c);
        }
        jobsDAO.saveJobsTempleteBetch(list);

    }

    @Override
    public Jobs findJobsById(java.lang.String uuid) {

        return jobsDAO.getJobs(uuid);

    }

    @Override
    public void removeJobs(String uuid) {
        jobsDAO.removeJobs(uuid);

    }

    @Override
    public Pager jobList(Pager pager) {

        List<JobListResp> list = jobsDAO.jobList(pager);
        int totalCount = jobsDAO.jobListCount(pager);

        for (JobListResp t : list) {
            String str = "";
            if (StringUtils.isEmptyOrNull(t.getRunWeek())) {
                str += "每天" + "\n";
            } else {
                str += taskServiceImpl.getWeekString(t.getRunWeek()) + "\n";
            }
            //如果是永久任务则beginDate和endDate是为空的，都有值则是按时间段执行的任务
            if (t.getBeginDate() == null && t.getEndDate() == null) {
                str += "永久任务\n";
            } else if (t.getBeginDate() != null && t.getEndDate() != null) {
                str += t.getBeginDateStr() + "~" + t.getEndDateStr() + "\n";
                str += t.getBeginTime() + "~" + t.getEndTime() + "\n";
            }

            t.setJobContent(str);
        }
        pager.setTotalCount(totalCount);
        pager.setResultList(list);

        return pager;
    }

    /**
     * @param pager type  0：有审批权限看到的是所有列表    1：当前用户自己的申请列表
     * @return
     */
    @Override
    public Pager ratifyList(Pager pager) {
        List<JobRatifyListResp> list = jobsDAO.ratifyList(pager);
        int count = jobsDAO.ratifyListCount(pager);
        pager.setResultList(list);
        pager.setTotalCount(count);
        return pager;
    }


    @Override
    public int selectTemplatedbIdCount(Integer templatedbId) {
        int count = jobsDAO.selectTemplatedbIdCount(templatedbId);
        return count;
    }

    /**
     * 布控：列表页的操作，暂停开启布控任务，取消订阅，删除，申请撤控
     *
     * @param
     * @return
     */
    @Override
    public ResponseBean updateOperate(UpdateOperateReq req) {

        // TODO: 2018/11/28 lxh  如果已经暂停的布控，点击开启，这个地方如果我这边直接变成休息中，会有延迟的，这个任务的状态是下次跑定时服务的时候才会更新
        if (req.getState() != null || req.getIsDeleted() != null) {
            //修改暂停开始状态
            jobsDAO.updateOperate(req);
        }

        /**** 订阅 ****/
        if (req.getSubscribeType() != null) {
            List<SysSubscribe> sysSubscribes = sysSubscribeDAO.queryByParam(req.getUserUuid(), req.getJobUuid(), null);

            SysSubscribe ss = new SysSubscribe();
            ss.setUuid(UuidUtil.getUuid());
            ss.setJobId(req.getJobUuid());
            ss.setUid(req.getUserUuid());
            if (req.getSubscribeType().intValue() == 1) {
                //订阅
                if (!CollectionUtils.isEmpty(sysSubscribes)) {
                    return ResultUtils.error(-1, "当前已经是订阅状态，无法再次订阅");
                }
                sysSubscribeDAO.saveSysSubscribe(ss);
            } else {
                //取消订阅
                if (CollectionUtils.isEmpty(sysSubscribes)) {
                    return ResultUtils.error(-1, "当前已经是取消订阅状态，无法再次取消订阅");
                }
                sysSubscribeDAO.removeSysSubscribeByUidAndJobId(ss);
            }


        }
        /**** 撤控 ****/
        if (req.getCancelJobs() != null) {
            if (StringUtils.isEmptyOrNull(req.getCancelJobReason())) {
                return ResultUtils.error(-1, "撤控理由不能为空");
            }
            req.setState(Constants.JOB_STATE_INTAKENCONTROL);
            req.setRatifyResult(0);
            jobsDAO.updateOperate(req);
        }
        dataInitService.init();
        return ResultUtils.success(null);
    }

    /**
     * 审批列表
     *
     * @param req
     * @return
     */
    @Override
    public ResponseBean ratifyJob(RatifyJobReq req) {

        dataInitService.init();
        int result = jobsDAO.ratifyJob(req);
        return result > 0 ? ResultUtils.success(null) : ResultUtils.UNKONW_ERROR();

    }

    /**
     * 布控：布控审批列表----不同布控状态的个数
     *
     * @param req
     * @return
     */
    @Override
    public ResponseBean jobListCount(JobListCountReq req) {

        List<JobListCountResp> list = new ArrayList<>();
        if (req.getIsRatifyList() != null && req.getIsRatifyList().intValue() == 1) {
            getListCount(list, 0, req, true);
            getListCount(list, 1, req, true);
            getListCount(list, 2, req, true);
        } else {
            getListCount(list, Constants.JOB_STATE_WAITSTART, req);
            getListCount(list, Constants.JOB_STATE_RUNNING, req);
            getListCount(list, Constants.JOB_STATE_PAUSE, req);
            getListCount(list, Constants.JOB_STATE_HADTAKENCONTROL, req);
            getListCount(list, Constants.JOB_STATE_INTAKENCONTROL, req);
            getListCount(list, Constants.JOB_STATE_DONE, req);
            getListCount(list, Constants.JOB_STATE_INREST, req);

        }

        return ResultUtils.success("resultList", list);
    }

    /**
     * 审批或者布控列表
     *
     * @param list         返回的list集合
     * @param state        布控或者审批状态
     * @param req          请求的入参
     * @param isRatifyList 是否是审批，默认是布控列表，可以不传
     * @return
     */
    private List<JobListCountResp> getListCount(List<JobListCountResp> list, Integer state, JobListCountReq req, boolean... isRatifyList) {
        Pager pager = new Pager();
        Map<String, String> map = new HashMap<>();
        map.put("querytype", req.getQuerytype() + "");
        if (isRatifyList != null && isRatifyList.length == 1 && isRatifyList[0] == true) {
            //审批列表
            map.put("ratifyResult", state + "");
            pager.setF(map);
            list.add(new JobListCountResp(state, jobsDAO.ratifyListCount(pager)));
        } else {
            //布控列表
            map.put("state", state + "");
            pager.setF(map);
            list.add(new JobListCountResp(state, jobsDAO.jobListCount(pager)));
        }


        return list;
    }


}