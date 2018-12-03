package com.sensing.core.dao;

import java.util.List;
import com.sensing.core.bean.Alarm;
import com.sensing.core.utils.Pager;

/**
 * @author mingxingyu
 */
public interface IAlarmDAO {
	public int saveAlarm(Alarm alarm) throws Exception;

	public Alarm getAlarm(java.lang.String uuid) throws Exception;

	public int removeAlarm(java.lang.String uuid) throws Exception;

	public int updateAlarm(Alarm alarm) throws Exception;

	public List<Alarm> queryList(Pager pager) throws Exception;

	public int selectCount(Pager pager) throws Exception;

}
