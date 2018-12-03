package com.sensing.core.service.impl;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.base.Joiner;
import com.sensing.core.aop.RequestInfoLog;
import com.sensing.core.bean.*;
import com.sensing.core.bean.req.RunningTaskCountReq;
import com.sensing.core.bean.req.StopTaskReq;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.thrift.cmp.bean.Result;
import com.sensing.core.utils.*;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.time.DateUtil;
import com.sensing.core.utils.time.TransfTimeUtil;
import oracle.sql.DATE;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.apache.poi.ss.formula.functions.Today;
import org.jsoup.helper.DataUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.dao.ISysUserDAO;
import com.sensing.core.dao.ITaskChannelDAO;
import com.sensing.core.dao.ITaskDAO;
import com.sensing.core.service.ITaskService;
import com.sensing.core.utils.task.TaskTimerTask;
import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @author wenbo
 */
@Service
public class TaskServiceImpl implements ITaskService {


    private static final Log log = LogFactory.getLog(ITaskService.class);

    @Resource
    public ITaskDAO taskDAO;
    @Resource
    public ITaskChannelDAO taskChannelDAO;
    @Resource
    public IChannelDAO channelDAO;
    @Resource
    public ISysUserDAO sysUserDAO;
    @Resource
    private IRpcLogService rcpLogService;
    @Resource
    public TaskTimerTask taskTimerTask;
    @Resource
    public CaptureServiceImpl captureServiceImpl;

    @Resource
    public RequestInfoLog requestInfoLog;

    public TaskServiceImpl() {
        super();
    }

    @Override
    public TaskRequest saveNewTask(TaskRequest request) {

        //保存task
        String taskUuid = UuidUtil.getUuid();
        request.getTask().setUuid(taskUuid);
        request.getTask().setRunWeek(orderRunWeek(request.getTask().getRunWeek()));
        int code = taskDAO.saveTask(request.getTask());
        //保存taskchannel 可能多条
        String[] split = request.getTaskChannel().getChannelUuid().split(",");
        for (int i = 0; i < split.length; i++) {
            request.getTaskChannel().setChannelUuid(split[i]);
            request.getTaskChannel().setUuid(UuidUtil.getUuid());
            request.getTaskChannel().setTaskUuid(taskDAO.getTask(taskUuid).getUuid());
            taskChannelDAO.saveTaskChannel(request.getTaskChannel());
        }
        //如果不是离线视频的任务，TODO: 2018/8/17 新建任务要考虑 如果是当前时间是在要执行任务的时间点内，则要立即通知抓拍任务
        if (request.getTask().getType() != 3) {
            int newState = 0;
            newState = taskTimerTask.getTaskState(request.getTask());
            if (newState != request.getTask().getState()) {
                taskTimerTask.startTask();
            }
        }
        return code > 0 ? request : null;
    }

    /**
     * 周期排序
     *
     * @param runWeek
     * @return
     */
    public String orderRunWeek(String runWeek) {
        String str = runWeek;
        if (!StringUtils.isEmptyOrNull(runWeek) && runWeek.contains(",")) {
            String[] runWeekArray = runWeek.split(",");
            List<String> runWeekArray2 = Arrays.asList(runWeekArray);
            Collections.sort(runWeekArray2);
            str = Joiner.on(",").join(runWeekArray2);

        }
        return str;
    }


    @Override
    public TaskRequest updateTask(TaskRequest request) {

        Task originalTask = taskDAO.getTask(request.getTask().getUuid());
        if (originalTask == null) {
            return null;
        }
        if (request.getTask().getIsDeleted() != null && request.getTask().getIsDeleted().intValue() == 1) {
            //删除
            return delTask(originalTask, request);
        } else {
            //修改
            return modifyTask(request);
        }

    }

    private TaskRequest delTask(Task originalTask, TaskRequest request) {
        // 处理中不能删除
        if (originalTask.getState().intValue() == Constants.TASK_STAT_RUNNING) {
            return null;
        }
        // TODO: 2018/10/11 lxh 处理中的任务不可删除，其他状态，删除时不需要通知通道关闭
        taskDAO.updateTask(request.getTask());
        taskChannelDAO.removeTaskChannelByTaskId(request.getTask().getUuid());
        return request;
    }

    private TaskRequest modifyTask(TaskRequest request) {
        taskDAO.updateTask(request.getTask());
        if (request.getTaskChannel() != null && !TextUtils.isEmpty(request.getTaskChannel().getChannelUuid())) {
            //关联通道做硬删除
            taskChannelDAO.removeTaskChannelByTaskId(request.getTask().getUuid());
            //保存taskchannel 可能多条
            String[] split = request.getTaskChannel().getChannelUuid().split(",");
            for (int i = 0; i < split.length; i++) {
                request.getTaskChannel().setChannelUuid(split[i]);
                request.getTaskChannel().setUuid(UuidUtil.getUuid());
                request.getTaskChannel().setTaskUuid(request.getTask().getUuid());
                taskChannelDAO.saveTaskChannel(request.getTaskChannel());
            }
        }
        return request;
    }

    @Override
    public Task updateState(Task task) {
        taskDAO.updateState(task);
        return task;
    }

    @Override
    public Task findTaskById(java.lang.String uuid) {
        return taskDAO.getTask(uuid);
    }

    @Override
    public void removeTask(String uuid) {
        taskDAO.removeTask(uuid);
        taskChannelDAO.removeTaskChannelByTaskId(uuid);
    }

    @Override
    public List<TaskChannel> getTaskChannelByChannelIds(List<String> list) {
        List<TaskChannel> task = taskChannelDAO.getTaskChannelByChannelIds(list);
        return task;
    }

    @Override
    public Pager query(Pager pager) {
        List<Task> list = taskDAO.queryList(pager);
        int totalCount = taskDAO.selectCount(pager);
        List<String> taskUuids = list.stream().map(a -> a.getUuid()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskUuids)) {
            pager.setTotalCount(0);
            return pager;
        }
        //task_channel
        List<TaskChannelResp> taskChannelList = taskChannelDAO.getTaskChannelByTaskIds(taskUuids);
        /*****  返回数据  ****/
        List<TaskRespTempList> resultList = new ArrayList<>();
        TaskRespTempList l;
        for (Task t : list) {
            l = new TaskRespTempList();
            List<String> channel = taskChannelList.stream().filter(a -> a.getTaskUuid().equals(t.getUuid())).map(b -> b.getChannel_name()).collect(Collectors.toList());
            l.setChannelNames(Joiner.on("、").join(channel));
            l.setAnaly_type(t.getAnalyType());
            l.setBegin_date(t.getBeginDate());
            l.setBegin_date_str(t.getBeginDateStr());
            l.setEnd_date(t.getEndDate());
            l.setEnd_date_str(t.getEndDateStr());
            l.setStart_time(t.getStartTime());
            l.setEnd_time(t.getEndTime());
            l.setCreateUserUuid(t.getCreateUser());
            l.setState(t.getState());
            l.setAnaly_start_time(t.getAnalyStartTime());
            l.setAnaly_end_time(t.getAnalyEndTime());
            l.setTaskName(t.getName());
            l.setTaskUuid(t.getUuid());
            l.setType(t.getType());
            l.setIsDel(t.getIsDeleted() == null ? 0 : t.getIsDeleted().intValue());
            l.setCreateUser(t.getCreateUser());
            l.setRunWeek(t.getRunWeek());
            l.setAnaly_begin_date(t.getBeginDate());
            l.setAnaly_end_date(t.getEndDate());
            l.setRunWeek(t.getRunWeek());


            resultList.add(l);
        }
        pager.setTotalCount(totalCount);
        /*****  给前端拼接字段  ****/
        pager.setResultList(fillTaskList2(resultList));
        return pager;
    }

    @Override
    public Pager queryListByName(Pager pager) {
        List<TaskRespTempList> list = taskDAO.queryListByName(pager);
        int count = taskDAO.selectCountByName(pager);
        pager.setTotalCount(count);
        /*****  给前端拼接字段  ****/
        pager.setResultList(fillTaskList2(list));
        return pager;
    }


    /**
     * 返回前段可以直接使用的数据集合
     *
     * @param taskResps
     * @return
     */

    public List<TaskRespList> fillTaskList2(List<TaskRespTempList> taskResps) {
        List<TaskRespList> list = new ArrayList<>();
        TaskRespList l = null;
        for (TaskRespTempList t : taskResps) {
            l = new TaskRespList();

            l.setTaskUuid(t.getTaskUuid());
            l.setTaskName(t.getTaskName());
            l.setState(t.getState());
            l.setCreateUser(t.getCreateUser());
            l.setIsDel(t.getIsDel());
            l.setType(t.getType());
            int type = t.getType() != null ? t.getType() : 0;
            /*****  通道 "名字A，名字CB，名字C，" ****/
            String channelNames = t.getChannelNames();
            if (!TextUtils.isEmpty(channelNames)) {
                List<String> cList = Arrays.asList(channelNames.split("、"));
                String channelDesc = "";
                if (cList.size() == 1) {
                    channelDesc = cList.get(0) + " ";
                } else if (cList.size() == 2) {
                    channelDesc = cList.get(0) + "、" + cList.get(1) + " ";
                } else if (cList.size() >= 3) {
                    channelDesc = cList.get(0) + "、" + cList.get(1) + "等";
                }
                channelDesc = channelDesc + cList.size() + "个通道";
                l.setChannelDesc(channelDesc);
                l.setChannelDescAll(t.getChannelNames());
                l.setChannelCount(cList.size());
            } else {
                l.setChannelDesc("暂无关联通道");
            }


            String analyType = t.getAnaly_type();
            if (!TextUtils.isEmpty(analyType)) {
                List<String> strings = Arrays.asList(analyType.split(","));
                List<String> anayTypes = strings.stream().map(a -> Constants.CAP_ANALY_TYPE.get(Integer.parseInt(a))).filter(x -> StringUtils.isNotEmpty(x)).collect(Collectors.toList());
                l.setTaskType(Joiner.on("、").join(anayTypes));
                l.setTaskTypeValue(analyType);
            }
            /*****  任务时段  ****/
            String taskTimeTop = "";
            String taskTimeCenter = "";
            String taskTimeBottom = "";
            if (type == 1) {
                //如果是永久任务则beginDate和endDate是为空的，都有值则是按时间段执行的任务
                if (t.getBegin_date() == null && t.getEnd_date() == null) {
                    taskTimeTop = "永久任务";
                } else if (t.getBegin_date() != null && t.getEnd_date() != null) {
                    taskTimeTop = t.getBegin_date_str() + "~" + t.getEnd_date_str() + "";
                    taskTimeBottom = t.getStart_time() + "~" + t.getEnd_time();
                }
                if (StringUtils.isEmptyOrNull(t.getRunWeek())) {
                    taskTimeCenter = "每天";
                } else {
                    taskTimeCenter = getWeekString(t.getRunWeek());
                }

            } else if (type == 2) {
                taskTimeTop = t.getBegin_date_str() + t.getStart_time();
                taskTimeCenter = t.getEnd_date_str() + t.getEnd_time();
            }
            l.setTaskTimeTop(taskTimeTop);
            l.setTaskTimeCenter(taskTimeCenter);
            l.setTaskTimeBottom(taskTimeBottom);

            /*****  分析时段  ****/

            String analyTime = "";
            if (type == 1) {
                analyTime = t.getAnaly_start_time() + "~" + t.getAnaly_end_time();
            } else if (type == 2) {
                analyTime = t.getAnaly_begin_date() + " " + t.getAnaly_start_time();
            }
            l.setAnalyTime(analyTime);
            /*****  状态  ****/
            l.setTaskState(Constants.TASK_STAT_MAP.get(t.getState()));
            SysUser sysUser = sysUserDAO.getSysUser(t.getCreateUser());
            if (sysUser != null && !TextUtils.isEmpty(sysUser.getUsername())) {
                l.setCreateUser(sysUser.getUsername());
            }
            list.add(l);
        }
        return list;
    }

    public String getWeekString(String runWeek) {
        String str = "";
        StringBuilder b = new StringBuilder();
        String[] split = runWeek.split(",");
        List<String> weekList = Arrays.asList(split);
        for (int i = 0; i < split.length; i++) {
            int week = Integer.parseInt(split[i]);
            switch (week) {
                case 1:
                    b.append("周一、");
                    break;
                case 2:
                    b.append("周二、");
                    break;
                case 3:
                    b.append("周三、");
                    break;
                case 4:
                    b.append("周四、");
                    break;
                case 5:
                    b.append("周五、");
                    break;
                case 6:
                    b.append("周六、");
                    break;
                case 7:
                    b.append("周日、");
                    break;
            }
        }
        return b.subSequence(0, b.length() - 1).toString();
    }

    @Override
    public TaskResp info(String uuid) {

        Task task = taskDAO.getTask(uuid);
        if (task == null) return null;
        //task_channel
        List<TaskChannelResp> taskChannelList = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(task.getUuid()));
        TaskResp taskResp = new TaskResp();
        taskResp.setTask(task);
        taskResp.setTaskChannel(taskChannelList);
        return taskResp;

    }

    @Override
    public List<Task> getUpdateStateTask(List<Integer> list) {

        List<Task> rest = taskDAO.getUpdateStateTask(list);
        return rest;
    }

    @Override
    public int getrunningtaskCount(RunningTaskCountReq req) {
        int count = taskDAO.getrunningtaskCount(req);
        return count;
    }

    /**
     * 停止任务（待启动和处理中才能停止）
     * 2018/9/21
     *
     * @param req
     * @return
     */
    @Override
    public ResponseBean stoptaskByUuId(StopTaskReq req) {
        int result = 1;
        Task task = taskDAO.getTask(req.getTaskUuid());
        if ((task == null) || task != null && task.getState().intValue() != req.getState().intValue()) {
            return ResultUtils.error(-1, "请刷新页面后重试");
        }

        Integer preState = task.getState();
        //设置成已完成即可
        task.setState(Constants.TASK_STAT_STOP);
        List<TaskChannelResp> stopChannel = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(task.getUuid()));
        /*** 正在进行中的任务要关闭通道 *****/
        if (req.getState().intValue() == Constants.TASK_STAT_RUNNING) {
            result = taskTimerTask.takeRestAndDoneTask(task, stopChannel);
            if (result == 0) {
                /***  通知抓拍异常 ****/
                task.setState(Constants.TASK_STAT_FAILEE);
            }
        }
        if (preState.intValue() != task.getState().intValue()) {
            taskDAO.setUpdateStateTask(task);
            taskTimerTask.setTaskRpcLog(task, preState, stopChannel);
        }

        return result > 0 ? ResultUtils.success(null) : ResultUtils.UNKONW_ERROR();
    }

    /**
     * 开启任务(已停止的任务启动，待启动和处理中才能停止)
     * 2018/9/21
     *
     * @param req
     * @return
     */
    @Override
    public ResponseBean startTaskByUuId(StopTaskReq req) {
        int result = 1;
        Task task = taskDAO.getTask(req.getTaskUuid());
        if ((task == null) || task != null && task.getState().intValue() != req.getState().intValue()) {
            return ResultUtils.error(-1, "请刷新页面后重试");
        }
        Integer preState = task.getState();
        List<TaskChannelResp> taskChannel = taskChannelDAO.getTaskChannelByTaskIds(Arrays.asList(task.getUuid()));
        task.setState(Constants.TASK_STAT_WAITSTART);
        // TODO: 2018/9/21 lxh 遗留bug， 处理中停止，在开启，休息中和待启动
        // 如果是，如果休息中的停止后，在开启就变成待启动，查mongo里该任务，
        taskDAO.setUpdateStateTask(task);
        taskTimerTask.setTaskRpcLog(task, req.getState(), taskChannel);

        taskTimerTask.startTask();

//        int result = taskTimerTask.openTask(task, taskChannel);
//        if (result == 0) {
//            /***  通知抓拍异常 ****/
//            task.setState(Constants.TASK_STAT_FAILEE);
//        } else {
//            result = 1;
//        }

//        if (preState.intValue() != task.getState().intValue()) {
//            taskDAO.setUpdateStateTask(task);
//            taskTimerTask.setTaskRpcLog(task, req.getState(), taskChannel);
//        }

        return result > 0 ? ResultUtils.success(null) : ResultUtils.UNKONW_ERROR();
    }

    @Override
    public List<Task> getTaskByName(String name) {
        List<Task> taskByName = taskDAO.getTaskByName(name);
        return taskByName;
    }

    @Override
    public Map getAnalyTimeByUuid(String taskUuid) {
        Task task = taskDAO.getTask(taskUuid);
        if (task == null) {
            return null;
        }
        Map<Long, Long> timeMap = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                long l = o1.longValue() - o2.longValue();
                if (l > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        Date starDate = null;
        Date endDate = null;


        if (task.getBeginDate() == null && task.getEndDate() == null) {
            starDate = task.getCreateTime();
            endDate = new Date();
            //去掉时分秒的限制
            starDate = DateUtil.removeHMS(starDate);
            endDate = DateUtil.removeHMS(endDate);
        } else if (task.getBeginDate() != null && task.getEndDate() != null) {
            /******************************************* 指定时间段任务 start **************************************************************/
            starDate = task.getBeginDate();
            endDate = task.getEndDate();
        }
        try {
            List<Integer> runWeek = Arrays.asList(task.getRunWeek().split(",")).stream().map(a -> Integer.parseInt(a)).collect(Collectors.toList());
            for (Integer w : runWeek) {
                timeMap.putAll(getWeekDate(starDate, endDate, w, task.getAnalyStartTime(), task.getAnalyEndTime(), task.getCreateTime()));
            }

            // TODO: 2018/9/26 lxh 如果运行周期是连续的周一到周日，时间是分段的，会影响查询效率，后期考虑是否合成一个
//            conbainTime(timeMap);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.info("查询任务的时间段==map==" + timeMap.toString());


        return timeMap;

    }


    public static Map<Long, Long> getWeekDate(Date sd, Date ed, int dayOfWeek, String analyStart, String analyEnd, Date createTime) throws ParseException {
        java.text.SimpleDateFormat dayf = new java.text.SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        // TODO: 2018/9/26 lxh 23:59:00,要改成 23：59：59这种样式
        analyEnd = analyEnd.substring(0, analyEnd.lastIndexOf(":") + 1) + "59";
        Map<Long, Long> map = new TreeMap<>();
        Calendar c = Calendar.getInstance();
        c.setTime(sd);
        int day = c.get(Calendar.DAY_OF_WEEK) - 1; //找出开始日期是星期几，需求不一样可修改
        List<Date> dateList = new ArrayList<Date>();
        if (day != dayOfWeek) {
            int dif = dayOfWeek < day ? (dayOfWeek - day + 7) : (dayOfWeek - day); //算出跟参数相差的星期的天数
            c.add(Calendar.DATE, dif);
        }
        long end1;
        long start1;
        while (!c.getTime().after(ed)) {
            dateList.add(c.getTime());

            Date time1 = DateUtil.StringToDate(dayf.format(c.getTime()) + " " + analyStart);
            if (time1.getTime() < createTime.getTime()) {
                time1 = createTime;
            }
            Date time2 = DateUtil.StringToDate(dayf.format(c.getTime()) + " " + analyEnd);

            log.info("查询任务的时间段" + DateUtil.DateToString(time1) + "~" + DateUtil.DateToString(time2));


            start1 = time1.getTime() / 1000;
            end1 = time2.getTime() / 1000;

            map.put(start1, end1);

            c.add(Calendar.DATE, 7);
        }
        return map;
    }

    /**
     * 将时间段合到一起，举例：2018-10-02 00:00:00 ~~ 2018-10-02 23:59:00 和 2018-10-03 00:00:00 ~~ 2018-10-03 23:59:00 合并成一个
     *
     * @param timeMap
     * @return
     */
    private Map<Long, Long> conbainTime(Map<Long, Long> timeMap) {
        // TODO: 2018/10/18 lxh 未自测
        List<KeyValueTemp> lists = new ArrayList<>();
        List<KeyValueTemp> afterLists = new ArrayList<>();
        for (Long key : timeMap.keySet()) {
            lists.add(new KeyValueTemp(key, timeMap.get(key)));
        }
        for (int i = 0; i < lists.size() - 1; i++) {
            if (lists.get(i).getValue() == lists.get(i + 1).getKey()) {

            } else {
                afterLists.add(lists.get(i));
            }
        }

        Map<Long, Long> afterMap = new LinkedHashMap<>();
        for (KeyValueTemp temp : afterLists) {
            afterMap.put(temp.getKey(), temp.getValue());
        }
        return afterMap;
    }

}