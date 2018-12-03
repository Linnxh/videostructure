package com.sensing.core.service;

import com.sensing.core.bean.*;
import com.sensing.core.bean.req.RunningTaskCountReq;
import com.sensing.core.bean.req.StopTaskReq;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;

import java.util.List;
import java.util.Map;

/**
 * @author wenbo
 */
public interface ITaskService {

    public TaskRequest saveNewTask(TaskRequest task);

    public TaskRequest updateTask(TaskRequest task);

    public Task updateState(Task task);

    public abstract Task findTaskById(java.lang.String uuid);

    public abstract void removeTask(java.lang.String uuid);

    public List<TaskChannel> getTaskChannelByChannelIds(List<String> list);

    public Pager query(Pager pager);

    public Pager queryListByName(Pager pager);

    public TaskResp info(String uuid);

    public List<Task> getUpdateStateTask(List<Integer> list);

    public int getrunningtaskCount(RunningTaskCountReq req);

    ResponseBean stoptaskByUuId(StopTaskReq req);

    ResponseBean startTaskByUuId(StopTaskReq req);

    List<Task> getTaskByName(String name);

    Map getAnalyTimeByUuid(String taskUuid);
}