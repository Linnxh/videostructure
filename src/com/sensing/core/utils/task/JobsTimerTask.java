package com.sensing.core.utils.task;


import com.sensing.core.alarm.DataInitService;
import com.sensing.core.bean.Jobs;
import com.sensing.core.bean.Task;
import com.sensing.core.dao.IJobsDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.impl.JobsServiceImpl;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.time.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.sensing.core.utils.Constants.JOB_STATE_RUNNING;
import static com.sensing.core.utils.Constants.TASK_STAT_RUNNING;

/**
 * 布控模块的定时任务
 */
@Component
public class JobsTimerTask {


    @Resource
    public IJobsDAO jobsDAO;

    @Resource
    public DataInitService dataInitService;
    private static final Log log = LogFactory.getLog(JobsTimerTask.class);

    public void startTask() {

        log.info("~~~~~~ jobsTimer ~~~~~~" + DateUtil.DateToString(new Date()));

        List<Jobs> jobs = jobsDAO.getUpdateStateJob(Arrays.asList(Constants.JOB_STATE_WAITSTART, Constants.JOB_STATE_RUNNING, Constants.JOB_STATE_INREST));

        //布控状态状态 10:待启动 20:布控中  30:暂停中 40:休息中 50:已撤控  60:撤控中 70:已完成

        for (Jobs j : jobs) {
            int newState = getJobState(j);
            j.setNewState(newState);
        }


        //布控中
        List<Jobs> runningList = jobs.stream().filter(j -> j.getState() != j.getNewState() && j.getNewState() == Constants.JOB_STATE_RUNNING).collect(Collectors.toList());


        //休息或者停止
        List<Jobs> stopList = jobs.stream().filter(j -> j.getState() != j.getNewState() && (j.getNewState() == Constants.JOB_STATE_INREST || j.getNewState() == Constants.JOB_STATE_DONE)).collect(Collectors.toList());


        //更新数据库里的状态
        runningList.addAll(stopList);
        if (!CollectionUtils.isEmpty(runningList)) {
            // TODO: 2018/11/28 lxh 更新alam数据
            dataInitService.init();
            //更新job
            jobsDAO.updateStateBetch(runningList);
        }


    }


    public int getJobState(Jobs job) {
        int nowState = job.getState().intValue();
        try {
            if (job.getState() == null || job.getRunWeek() == null) {
                return nowState;
            }
            if (StringUtils.isEmptyOrNull(job.getBeginTime()) || job.getBeginTime().split(":").length == 2) {
                return nowState;
            }
            /****  得到任务状态  ***/
            nowState = getTaskStateByDate(job, new Date(), 1);

            java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");//设置日期格式
            Date startTime = timef.parse(job.getBeginTime());
            Date endTime = timef.parse(job.getEndTime());
            Date t1 = timef.parse("00:00:00");
            Date t2 = timef.parse("00:05:00");
            if (startTime.getTime() >= t1.getTime() && startTime.getTime() < t2.getTime()) {
                /****  当前时间推迟2（定时任务时间间隔）分钟得到任务状态（如果开启时间是00点到0点5分之间的任务）  ***/
                int nowStateAfter5 = getTaskStateByDate(job, DateUtil.addMinute(new Date(), PropUtils.getInt("job.update.time")), 2);
                //如果5分钟之后是运行状态，则提前将其打开，注意，这种情况下，开始时间就不用-5分钟（00:00:00 减之后会错）
                if (nowStateAfter5 == Constants.TASK_STAT_RUNNING.intValue()) {
                    nowState = nowStateAfter5;
                }
            }

        } catch (ParseException e) {
            log.error("JobsTimerTask，修改任务状态异常====" + StringUtils.getStackTrace(e));
        }

        return nowState;
    }

    /**
     * 根据时间判断task的state值，注：该方法外部不可使用
     */
    private int getTaskStateByDate(Jobs job, Date nowDate, int type) throws ParseException {
        int nowState = job.getState();
        java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");//设置日期格式
        java.text.SimpleDateFormat dayf = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date startTime = timef.parse(job.getBeginTime());
        Date endTime = timef.parse(job.getEndTime());
        Date nowTime = timef.parse(timef.format(nowDate));
        // TODO: 2018/8/16   lxh   0:待启动，1:处理中，2:休息中，3:已暂停，4:已停止，5:已完成，6:失败
        /******************************************* 永久执行任务 **************************************************************/
        if (job.getBeginDate() == null && job.getEndDate() == null) {
            int week = DateUtil.getWeek(nowDate);
            List<String> runWeeks = Arrays.asList(job.getRunWeek().split(","));
            if (runWeeks.contains(week + "")) {
                if (nowTime.getTime() >= DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("job.update.time") : 0).getTime() && nowTime.getTime() <= endTime.getTime()) {
                    //当前任务应该在进行中
                    nowState = JOB_STATE_RUNNING;
                } else if (nowTime.getTime() > endTime.getTime()) {
                    //改成休息中 或者 待启动
                    if (job.getState() == Constants.JOB_STATE_WAITSTART) {
                        nowState = Constants.JOB_STATE_WAITSTART;
                    } else if (job.getState() == JOB_STATE_RUNNING) {
                        nowState = Constants.JOB_STATE_INREST;
                    } else {
                        nowState = Constants.JOB_STATE_INREST;
                    }
                } else if (nowTime.getTime() < DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("job.update.time") : 0).getTime()) {
                    //未到任务开始时间
                    nowState = getNotStartState(job, nowState, type);
                }
            } else {
                nowState = getNotStartState(job, nowState, type);
            }
        } else if (job.getBeginDate() != null && job.getEndDate() != null) {
            /******************************************* 指定时间段任务 start **************************************************************/

            // TODO: 2018/8/16   lxh   0:待启动，1:处理中，2:休息中，3:已暂停，4:已停止，5:已完成，6:失败
            if ((dayf.parse(dayf.format(nowDate)).getTime()) < job.getBeginDate().getTime()) {
                //当前日期在任务开始日期之前
                nowState = Constants.JOB_STATE_WAITSTART;
            } else if (dayf.parse(dayf.format(nowDate)).getTime() >= job.getBeginDate().getTime() && dayf.parse(dayf.format(nowDate)).getTime() <= job.getEndDate().getTime()) {
                //当前日期在任务开始日期之间
                int week = DateUtil.getWeek(nowDate);
                List<String> runWeeks = Arrays.asList(job.getRunWeek().split(","));

                if (runWeeks.contains(week + "")) {
                    if (nowTime.getTime() < DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("job.update.time") : 0).getTime()) {
                        nowState = getNotStartState(job, nowState, type);
                    } else if (nowTime.getTime() >= DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("job.update.time") : 0).getTime() && nowTime.getTime() <= endTime.getTime()) {
                        //当前任务应该在进行中
                        nowState = JOB_STATE_RUNNING;
                    } else if (nowTime.getTime() > endTime.getTime()) {
                        //改成休息中  待启动  已完成
                        if (job.getState() == Constants.JOB_STATE_WAITSTART) {
                            nowState = Constants.JOB_STATE_WAITSTART;
                        } else if (job.getState() == JOB_STATE_RUNNING) {
                            //之前是：运行中，要修改成休息中或者已完成,取决于下次任务是否能执行
                            List<String> runWeeks2 = new ArrayList<>();
                            runWeeks2.addAll(runWeeks);
                            runWeeks2.addAll(runWeeks);
                            int i = runWeeks.indexOf(week + "") + 1;
                            int nextWeek = Integer.valueOf(runWeeks2.get(i)).intValue();
                            //下次执行的日期
                            Date nextRunTime = null;
                            if (nextWeek > week) {
                                nextRunTime = DateUtil.addDay(nowDate, nextWeek - week);
                            } else if (nextWeek < week) {
                                nextRunTime = DateUtil.addDay(nowDate, week - nextWeek);
                            } else if (nextWeek == week) {
                                nextRunTime = DateUtil.addDay(nowDate, week);
                            }

                            if (dayf.parse(dayf.format(nextRunTime)).getTime() >= job.getBeginDate().getTime() && dayf.parse(dayf.format(nextRunTime)).getTime() <= job.getEndDate().getTime()
                                    && dayf.parse(dayf.format(nextRunTime)).getTime() > job.getEndDate().getTime()) {
                                // 已完成
                                nowState = Constants.JOB_STATE_DONE;
                            } else if (dayf.parse(dayf.format(nextRunTime)).getTime() >= job.getEndDate().getTime()) {
                                nowState = Constants.JOB_STATE_DONE;
                            } else {
                                // 休息中
                                nowState = Constants.JOB_STATE_INREST;
                            }
                        } else if (job.getState() == Constants.JOB_STATE_INREST) {
                            nowState = Constants.JOB_STATE_INREST;
                        } else {
                            nowState = Constants.JOB_STATE_INREST;
                        }
                    }
                } else {
                    nowState = getNotStartState(job, nowState, type);
                }
            } else if ((dayf.parse(dayf.format(nowDate)).getTime()) > job.getEndDate().getTime()) {
                nowState = Constants.JOB_STATE_DONE;
            }
            /******************************************* 指定时间段任务 end **************************************************************/
        }

        return nowState;
    }


    /**
     * @param job
     * @param nowState
     * @return
     */
    private int getNotStartState(Jobs job, int nowState, int type) throws ParseException {
        //当前任务是休息中
        if (job.getState() == Constants.JOB_STATE_WAITSTART) {
            nowState = Constants.JOB_STATE_WAITSTART;
        } else if (job.getState() == Constants.JOB_STATE_RUNNING) {
            nowState = Constants.JOB_STATE_INREST;
        } else if (job.getState() == Constants.JOB_STATE_INREST) {
            nowState = Constants.JOB_STATE_INREST;
        } else if (job.getState() == Constants.JOB_STATE_PAUSE) {
            nowState = Constants.JOB_STATE_INREST;
        } else if (job.getState() == Constants.TASK_STAT_FAILEE) {
            //之前是未启动过，当前是待启动状态，之前启动过，当前是休息中
            nowState = Constants.JOB_STATE_INREST;
        }
        return nowState;
    }


}
