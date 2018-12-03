package com.sensing.core.service;

import com.sensing.core.bean.Alarm;
import com.sensing.core.utils.Pager;

/**
 *@author mingxingyu
 */
public interface IAlarmService {

	public abstract Alarm saveNewAlarm(Alarm alarm)  throws Exception;

	public Alarm updateAlarm(Alarm alarm)  throws Exception;

	public abstract Alarm findAlarmById(java.lang.String uuid) throws Exception;

	public abstract void removeAlarm(java.lang.String uuid) throws Exception;

	public Pager queryPage(Pager pager) throws Exception;

}