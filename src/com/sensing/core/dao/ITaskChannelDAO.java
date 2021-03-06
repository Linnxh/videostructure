package com.sensing.core.dao;

import java.util.List;

import com.sensing.core.bean.TaskChannel;
import com.sensing.core.bean.TaskChannelResp;
import com.sensing.core.utils.Pager;

/**
 * @author wenbo
 */
public interface ITaskChannelDAO {
    public int saveTaskChannel(TaskChannel taskChannel) ;

    public TaskChannel getTaskChannel(java.lang.String uuid) ;

    public List<TaskChannelResp> getTaskChannelByTaskIds(List<String> list) ;

    public List<TaskChannel> getTaskChannelByChannelIds(List<String> list) ;

    public int removeTaskChannel(java.lang.String uuid) ;

    public int removeTaskChannelByTaskId(java.lang.String task_uuid) ;

    public int updateTaskChannel(TaskChannel taskChannel) ;

    public List<TaskChannel> queryList(Pager pager) ;

    public int selectCount(Pager pager) ;
    
    public int deleteByChannelId(String uuid);
}
