package com.sensing.core.dao;

import java.util.List;

import com.sensing.core.bean.req.RunningTaskCountReq;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskRespTempList;
import com.sensing.core.utils.Pager;

/**
 * @author wenbo
 */
public interface ITaskDAO {
    public int saveTask(Task task);

    public Task getTask(java.lang.String uuid);

    /**
     * 通过任务名称获得任务
     * @param taskName
     * @return
     */
    public List<Task> getTaskByName(java.lang.String taskName);

    public int removeTask(java.lang.String uuid);

    public int updateTask(Task task);

    public int updateState(Task task);

    public List<Task> queryList(Pager pager);

    public List<TaskRespTempList> queryListByName(Pager pager);

    public int selectCount(Pager pager);

    public int selectCountByName(Pager pager);

    public List<Task> getUpdateStateTask(List<Integer> list);

    public int setUpdateStateTasks(List<Task> list);

    public int setUpdateStateTask(Task task);

    public int getrunningtaskCount(RunningTaskCountReq req);

}
