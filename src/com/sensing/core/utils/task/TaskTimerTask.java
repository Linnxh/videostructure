package com.sensing.core.utils.task;


import static com.sensing.core.utils.Constants.TASK_STAT_RUNNING;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Joiner;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskChannelResp;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.dao.IRpcLogDAO;
import com.sensing.core.dao.ITaskChannelDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.impl.CaptureThriftServiceImpl;
import com.sensing.core.service.impl.ChannelServiceImpl;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.WebUtil;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.time.DateUtil;

/**
 * 任务模块的定时任务
 */
@Component
public class TaskTimerTask {

    private static final Log log = LogFactory.getLog(TaskTimerTask.class);

    @Resource
    public ITaskDAO taskDAO;
    @Resource
    public ITaskChannelDAO taskChannelDAO;
    @Resource
    public IChannelDAO channelDAO;
    @Resource
    public ChannelServiceImpl channelServiceImpl;
    @Resource
    public CaptureThriftServiceImpl captureThriftServiceImpl;
    @Resource
    public IRpcLogDAO rpcLogDAO;
    //是否在处理中 新建任务要考虑（如果是当前时间是在要执行任务的时间点内，则要立即通知抓拍任务）也有可能会掉改方法，保证线程安全
    private static volatile boolean isProcess = false;

    /**
     * 修改task状态的定时任务
     */
    public void startTask() {
        try {
            if (isProcess) {
                log.info("~~~~~~ taskTimer ~~~~~~isProcess为" +isProcess+"~~当前有任务正在执行中");
                return;
            }
            isProcess = true;
            log.info("~~~~~~ taskTimer ~~~~~~" + DateUtil.DateToString(new Date()));

            // TODO: 2018/8/15 lxh  查询当前时间段有效的任务(type in (1)  is_del=0  state in（待启动，处理中，休息中,失败) )192.168.1.217
            List<Task> tasks = taskDAO.getUpdateStateTask(Arrays.asList(Constants.TASK_STAT_WAITSTART, TASK_STAT_RUNNING, Constants.TASK_STAT_INREST, Constants.TASK_STAT_FAILEE));

            for (Task t : tasks) {
                int preState = t.getState();
                /*** 得到任务状态 ***/
                int nowState = getTaskState(t);
                List<TaskChannelResp> taskChannel = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(t.getUuid()));
//                log.info("===== taskTimer ===" + "关联通道个数" + (CollectionUtils.isEmpty(taskChannel) ? "0" : taskChannel.size()) + " ===入参==" + t.toString() + "==系统时间==" + new Date() + "==之前状态值==" + Constants.TASK_STAT_MAP.get(t.getState()) + "====要修改成的状态值====" + nowState + "==" + Constants.TASK_STAT_MAP.get(nowState));
                if (CollectionUtils.isEmpty(taskChannel)) {
                    // 任务存在，任务关联的通道是不存在的，这种情况下，应该将该通道设置成已完成状态（产生原因：1、删通道以后，对应的任务没有其他关联的通道；2、录入的时候taskchannel里的cnanneluuid录入错误）
                    t.setState(Constants.TASK_STAT_DONE);
                    int i = taskDAO.setUpdateStateTask(t);
                    if (i > 0) {
                        setTaskRpcLog(t, preState, taskChannel);
                    }
                    continue;
                }


                /*******  如果是状态有变化才处理，如果是一样则不处理  ***********/
                if (nowState == preState) {
                    continue;
                }
                t.setState(nowState);
                log.info("===== taskTimer  ===修改状态值======" + t.getUuid() + "====" + t.getName() + "==之前状态值==" + Constants.TASK_STAT_MAP.get(preState) + "====要修改成的状态值====" + nowState + "==" + Constants.TASK_STAT_MAP.get(nowState) + "==系统时间=" + DateUtil.DateToString(new Date()));

                int result = 1;
                if (t.getState().intValue() == TASK_STAT_RUNNING.intValue()) {
                    /****  要开启的任务   ***/
                    result = openTask(t, taskChannel);
                } else if (t.getState().intValue() == Constants.TASK_STAT_DONE.intValue() || t.getState().intValue() == Constants.TASK_STAT_INREST.intValue()) {
                    /****  要  休息||已完成  的任务    ***/
                    result = takeRestAndDoneTask(t, taskChannel);
                }
                if (result <= 0) {
                    /***  通知抓拍异常 ****/
                    t.setState(Constants.TASK_STAT_FAILEE);
                }
                if (t.getState().intValue() != preState) {
                    taskDAO.setUpdateStateTask(t);
                    setTaskRpcLog(t, preState, taskChannel, result == -1 ? "抓拍通过个数超限" : "");
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage() + StringUtils.getStackTrace(e));
            try {
                setRPcLog(new RpcLog("定时任务", "失败", StringUtils.getStackTrace(e), 3));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            isProcess = false;
        }


    }


    /**
     * 要  休息||已完成  的任务
     *
     * @param t
     * @param taskChannel
     * @return 1:通知抓拍正常   0：通知抓拍异常
     */
    public int takeRestAndDoneTask(Task task, List<TaskChannelResp> taskChannel) {

        List<String> runningChannelIds = getRunTaskChannelIds(task.getUuid(), 1);
        // TODO: 2018/8/28 lxh 正在运行的通道，查看是cap_state是否为1，不为1，则打开
        List<TaskChannelResp> tcList = new ArrayList<>();
        for (TaskChannelResp tc : taskChannel) {
            // 查看通道是否被其他任务引用，当前正在使用中的通道不能关闭
            if ((runningChannelIds == null) || (runningChannelIds != null && !runningChannelIds.contains(tc.getChannelUuid()))) {
                tcList.add(tc);
            }
        }
        if (!CollectionUtils.isEmpty(tcList)) {
            return updateChannel(tcList, task, Constants.CAP_STAT_CLOSE);
        } else {
            //为空就不用通知抓拍
            return 1;
        }
    }

    /**
     * 要开启的任务
     *
     * @param t
     * @param taskChannel
     * @return 1:通知抓拍正常   0：通知抓拍异常  -1：当前通道运行个数超过5个
     */
    public int openTask(Task t, List<TaskChannelResp> taskChannel) {
        int result = 1;
        // TODO: 2018/9/28 lxh  计数，如果超过5路就添加失败,先注释掉
//        List<String> runningChannelIds = getRunTaskChannelIds(task.getUuid(), 1);
//        if (!CollectionUtils.isEmpty(runningChannelIds) && runningChannelIds.size() >= 5) {
//            return -1;
//        }
//        int runningChannelCount = runningChannelIds.size();
        List<TaskChannelResp> tcList = new ArrayList<>();
        for (TaskChannelResp tc : taskChannel) {
            if (tc.getCap_stat() == null || tc.getCap_stat().intValue() == Constants.CAP_STAT_CLOSE.intValue()) {
                tcList.add(tc);
            }
        }
        if (!CollectionUtils.isEmpty(tcList)) {
            return updateChannel(tcList, t, Constants.CAP_STAT_OPEN);
        } else {
            //为空就不用通知抓拍
            return 1;
        }
    }

    /**
     * @param tc          通道
     * @param t           当前的任务
     * @param newCapState 1：开启通道  0：关闭通道
     */
    private int updateChannel(List<TaskChannelResp> tc, Task task, int newCapState) {
        int result = 1;
        String errorMsg = "";
        CapReturn capReturn = null;
        List<String> channelIds = tc.stream().map(c -> c.getChannelUuid()).collect(Collectors.toList());
        try {
            /*** 更新抓拍 ***/
            capReturn = captureThriftServiceImpl.OpenCloseChannels(channelIds, newCapState, 0);
            if (capReturn.res < 0) {
                result = 0;
                errorMsg = capReturn.getMsg().substring(0, 1000);
                log.error("通知抓拍异常===" + errorMsg);
            } else {
                result = 1;
            }
        } catch (Exception e) {
            errorMsg = StringUtils.getStackTrace(e).substring(0, 1000);
            log.error("通知抓拍异常===" + errorMsg);
            result = 0;
        }

        if (result == 1) {
            Channel c = new Channel();
            c.setModifyTime(new Date().getTime() / 1000);
            c.setCapStat(newCapState);
            for (TaskChannelResp t : tc) {
                c.setUuid(t.getChannelUuid());
                channelDAO.updateChannel(c);
            }
        }
        setChannelRpcLog(tc, task, newCapState == 1 ? 0 : 1, capReturn, errorMsg);

        return result;
    }

    /**
     * 得到正在运行中的任务
     *
     * @param taskUuid 任务的uuid
     * @param type     1：移除任务本身 0：不移除任务本身   (正在处理中的任务置成已完成状态，要排除掉自身)
     * @return
     */
    public List<String> getRunTaskChannelIds(String taskUuid, int type) {
        // 查询当前正在被使用的通道
        Pager pager = new Pager();
        Map<String, String> map = new HashMap<>();
        map.put("state", TASK_STAT_RUNNING + "");
        map.put("type", 1 + "");
        pager.setF(map);
        pager.setPageFlag(null);
        List<Task> runningTasks = taskDAO.queryList(pager);
        //移除掉自己任务本身
        if (type == 1) {
            runningTasks.removeIf(a -> a.getUuid().equals(taskUuid));
        }
        List<String> runningChannelIds = null;
        if (!CollectionUtils.isEmpty(runningTasks)) {
            List<TaskChannelResp> runningChannel = taskChannelDAO.getTaskChannelByTaskIds(runningTasks.stream().map(a -> a.getUuid()).collect(Collectors.toList()));
            runningChannelIds = runningChannel.stream().map(a -> a.getChannelUuid()).collect(Collectors.toList());
        }
        return runningChannelIds;
    }


    /****
     * 设置任务状态值
     */
    public int getTaskState(Task task) {
        int nowState = task.getState().intValue();
        try {
            if (task.getState() == null || task.getType() == null || task.getRunWeek() == null) {
                return nowState;
            }
            if (StringUtils.isEmptyOrNull(task.getAnalyStartTime()) || task.getAnalyStartTime().split(":").length == 2) {
                return nowState;
            }
            /****  得到任务状态  ***/
            nowState = getTaskStateByDate(task, new Date(), 1);

            java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");//设置日期格式
            Date startTime = timef.parse(task.getAnalyStartTime());
            Date endTime = timef.parse(task.getAnalyEndTime());
            Date t1 = timef.parse("00:00:00");
            Date t2 = timef.parse("00:05:00");
            if (startTime.getTime() >= t1.getTime() && startTime.getTime() < t2.getTime()) {
                /****  当前时间推迟2（定时任务时间间隔）分钟得到任务状态（如果开启时间是00点到0点5分之间的任务）  ***/
                int nowStateAfter5 = getTaskStateByDate(task, DateUtil.addMinute(new Date(), PropUtils.getInt("task.update.time")), 2);
                //如果5分钟之后是运行状态，则提前将其打开，注意，这种情况下，开始时间就不用-5分钟（00:00:00 减之后会错）
                if (nowStateAfter5 == Constants.TASK_STAT_RUNNING.intValue()) {
                    nowState = nowStateAfter5;
                }
            }

        } catch (ParseException e) {
            log.error("TaskTimerTask，修改任务状态异常====" + StringUtils.getStackTrace(e));
        }

        return nowState;
    }

    /**
     * 根据时间判断task的state值，注：该方法外部不可使用
     */
    private int getTaskStateByDate(Task task, Date nowDate, int type) throws ParseException {
        int nowState = task.getState();
        java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");//设置日期格式
        java.text.SimpleDateFormat dayf = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date startTime = timef.parse(task.getAnalyStartTime());
        Date endTime = timef.parse(task.getAnalyEndTime());
        Date nowTime = timef.parse(timef.format(nowDate));
        // TODO: 2018/8/16   lxh   0:待启动，1:处理中，2:休息中，3:已暂停，4:已停止，5:已完成，6:失败
        /******************************************* 永久执行任务 **************************************************************/
        if (task.getBeginDate() == null && task.getEndDate() == null) {
            int week = DateUtil.getWeek(nowDate);
            List<String> runWeeks = Arrays.asList(task.getRunWeek().split(","));
            if (runWeeks.contains(week + "")) {
                if (nowTime.getTime() >= DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("task.update.time") : 0).getTime() && nowTime.getTime() <= endTime.getTime()) {
                    //当前任务应该在进行中
                    nowState = TASK_STAT_RUNNING;
                } else if (nowTime.getTime() > endTime.getTime()) {
                    //改成休息中 或者 待启动
                    if (task.getState() == Constants.TASK_STAT_WAITSTART) {
                        nowState = Constants.TASK_STAT_WAITSTART;
                    } else if (task.getState() == TASK_STAT_RUNNING) {
                        nowState = Constants.TASK_STAT_INREST;
                    } else {
                        nowState = Constants.TASK_STAT_INREST;
                    }
                } else if (nowTime.getTime() < DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("task.update.time") : 0).getTime()) {
                    //未到任务开始时间
                    nowState = getNotStartState(task, nowState, type);
                }
            } else {
                nowState = getNotStartState(task, nowState, type);
            }
        } else if (task.getBeginDate() != null && task.getEndDate() != null) {
            /******************************************* 指定时间段任务 start **************************************************************/

            // TODO: 2018/8/16   lxh   0:待启动，1:处理中，2:休息中，3:已暂停，4:已停止，5:已完成，6:失败
            if ((dayf.parse(dayf.format(nowDate)).getTime()) < task.getBeginDate().getTime()) {
                //当前日期在任务开始日期之前
                nowState = Constants.TASK_STAT_WAITSTART;
            } else if (dayf.parse(dayf.format(nowDate)).getTime() >= task.getBeginDate().getTime() && dayf.parse(dayf.format(nowDate)).getTime() <= task.getEndDate().getTime()) {
                //当前日期在任务开始日期之间
                int week = DateUtil.getWeek(nowDate);
                List<String> runWeeks = Arrays.asList(task.getRunWeek().split(","));

                if (runWeeks.contains(week + "")) {
                    if (nowTime.getTime() < DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("task.update.time") : 0).getTime()) {
                        nowState = getNotStartState(task, nowState, type);
                    } else if (nowTime.getTime() >= DateUtil.addMinute(startTime, type == 1 ? -PropUtils.getInt("task.update.time") : 0).getTime() && nowTime.getTime() <= endTime.getTime()) {
                        //当前任务应该在进行中
                        nowState = TASK_STAT_RUNNING;
                    } else if (nowTime.getTime() > endTime.getTime()) {
                        //改成休息中  待启动  已完成
                        if (task.getState() == Constants.TASK_STAT_WAITSTART) {
                            nowState = Constants.TASK_STAT_WAITSTART;
                        } else if (task.getState() == TASK_STAT_RUNNING) {
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

                            if (dayf.parse(dayf.format(nextRunTime)).getTime() >= task.getBeginDate().getTime() && dayf.parse(dayf.format(nextRunTime)).getTime() <= task.getEndDate().getTime()
                                    && dayf.parse(dayf.format(nextRunTime)).getTime() > task.getEndDate().getTime()) {
                                // 已完成
                                nowState = Constants.TASK_STAT_DONE;
                            } else if (dayf.parse(dayf.format(nextRunTime)).getTime() >= task.getEndDate().getTime()) {
                                nowState = Constants.TASK_STAT_DONE;
                            } else {
                                // 休息中
                                nowState = Constants.TASK_STAT_INREST;
                            }
                        } else if (task.getState() == Constants.TASK_STAT_INREST) {
                            nowState = Constants.TASK_STAT_INREST;
                        } else {
                            nowState = Constants.TASK_STAT_INREST;
                        }
                    }
                } else {
                    nowState = getNotStartState(task, nowState, type);
                }
            } else if ((dayf.parse(dayf.format(nowDate)).getTime()) > task.getEndDate().getTime()) {
                nowState = Constants.TASK_STAT_DONE;
            }
            /******************************************* 指定时间段任务 end **************************************************************/
        }

        return nowState;
    }

    /**
     * @param task
     * @param nowState
     * @return
     */
    private int getNotStartState(Task task, int nowState, int type) throws ParseException {
        //当前任务是休息中
        if (task.getState() == Constants.TASK_STAT_WAITSTART) {
            nowState = Constants.TASK_STAT_WAITSTART;
        } else if (task.getState() == Constants.TASK_STAT_RUNNING) {
            nowState = Constants.TASK_STAT_INREST;
        } else if (task.getState() == Constants.TASK_STAT_INREST) {
            nowState = Constants.TASK_STAT_INREST;
        } else if (task.getState() == Constants.TASK_STAT_STOP) {
            nowState = Constants.TASK_STAT_INREST;
//                java.text.SimpleDateFormat timef = new java.text.SimpleDateFormat("HH:mm:ss");//设置日期格式
//                java.text.SimpleDateFormat dayf = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
//
//                Date nowDate = dayf.parse(dayf.format(new Date()));
//                Date nowTime = timef.parse(timef.format(new Date()));
//                Date endTime = timef.parse(task.getAnalyEndTime());
//                Date startTime = timef.parse(task.getAnalyStartTime());
//
//                Date createDate = task.getCreateTime();
//                Date createTime = timef.parse(timef.format(createDate));
//
//                int nowWeek = DateUtil.getWeek(new Date());
//
//                List<Integer> runWeek = Arrays.asList(task.getRunWeek().split(",")).stream().map(a -> Integer.parseInt(a)).collect(Collectors.toList());
//
//                int beginWeek = DateUtil.getWeek(createDate);
//
//
//                if (task.getBeginDate() == null && task.getEndDate() == null) {
//                    //永久任务，
//
//                    if (runWeek.contains(beginWeek) && createTime.getTime() >= endTime.getTime()) {
//                        beginWeek = +1;
//                    }
//                    if (runWeek.contains(nowWeek) && nowTime.getTime() >= startTime.getTime()) {
//                        nowWeek = -1;
//                    }
//                    List<Integer> passWeek = new ArrayList<>();
//                    if (nowWeek > beginWeek) {
//                        for (int i = beginWeek; i <= nowWeek; i++) {
//                            passWeek.add(i);
//                        }
//                    } else {
//                        for (int i = nowWeek; i <= beginWeek; i++) {
//                            passWeek.add(i);
//                        }
//                    }
//
//                    boolean is_processed = false;
//                    for (int w : passWeek) {
//                        if (runWeek.contains(w)) {
//                            is_processed = true;
//                            break;
//                        }
//                    }
//                    if (is_processed) {
//                        nowState = Constants.TASK_STAT_INREST;
//                    } else {
//                        nowState = Constants.TASK_STAT_WAITSTART;
//                    }
//                } else if (task.getBeginDate() != null && task.getEndDate() != null) {
//                    if ((dayf.parse(dayf.format(new Date())).getTime()) < task.getBeginDate().getTime()) {
//                        //当前日期在任务开始日期之前
//                        nowState = Constants.TASK_STAT_WAITSTART;
//                    }
//
//                }

        } else if (task.getState() == Constants.TASK_STAT_FAILEE) {
            //之前是未启动过，当前是待启动状态，之前启动过，当前是休息中
            nowState = Constants.TASK_STAT_INREST;
        }
        return nowState;
    }

    /**
     * 记录日志：修改打开关闭通道
     *
     * @Author: LXH
     * @Date: 2018/9/14
     * 修改任务类型
     */
    public void setChannelRpcLog(List<TaskChannelResp> tcs, Task task, int preState, CapReturn capReturn, String errorMsg) {
        RpcLog rl = new RpcLog();
        if (capReturn == null) {
            rl.setResult("失败");
            rl.setType(3);
            rl.setErrorMsg(errorMsg);
        } else {
            rl.setResult(capReturn.res >= 0 ? "成功" : "失败");
            //type=3,运行日志
            if (capReturn.res < 0) {
                rl.setType(3);
                rl.setErrorMsg(capReturn.msg);
            } else {
                rl.setType(2);
            }
        }
        rl.setName("OpenCloseChannels");
        rl.setRpcType("thrift");
        rl.setTodo(preState == 0 ? "打开通道" : "关闭通道");
        //rl.setName(c.getUuid());

        List<String> nameAndUuidList = tcs.stream().map(a -> a.getChannel_name() + "==" + a.getChannelUuid() + "==").collect(Collectors.toList());
        rl.setMemo("==channelUuid==" + Joiner.on(",").join(nameAndUuidList) + "==之前状态==" + preState + "==修改之后的状态值==" + ((preState == 0) ? 1 + "" : 0 + "") + "==关联task==" + task.getUuid() + task.getName());
        setRPcLog(rl);
    }

    /**
     * 记录日志：修改任务状态值
     *
     * @Author: LXH
     * @Date: 2018/9/14
     */
    public void setTaskRpcLog(Task t, int preState, List<TaskChannelResp> taskChannel, String... memoExtend) {
        RpcLog rl = new RpcLog();
        rl.setResult("成功");
        rl.setRpcType("spring-task");
        rl.setTodo("修改任务状态值");
        rl.setType(2);
        rl.setMemo((memoExtend != null && memoExtend.length > 1) ? memoExtend[0] : "" + t.toString() + "关联通道个数" + (CollectionUtils.isEmpty(taskChannel) ? "0" : taskChannel.size()) + "==之前状态值==" + Constants.TASK_STAT_MAP.get(preState) + "==修改之后的状态值==" + Constants.TASK_STAT_MAP.get(t.getState()));
        setRPcLog(rl);
    }

    public void setRPcLog(RpcLog rl) {
        rl.setCallTime(new Date());
        rl.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
        rl.setModule(Constants.SEVICE_MODEL_TASK);
        rl.setIp(WebUtil.getLocalIP());
        rpcLogDAO.saveRpcLog(rl);
    }

}

