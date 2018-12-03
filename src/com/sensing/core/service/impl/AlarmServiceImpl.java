package com.sensing.core.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.sensing.core.bean.Alarm;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.service.IAlarmService;
import com.sensing.core.dao.IAlarmDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@author mingxingyu
 */
@Service
public class AlarmServiceImpl implements IAlarmService{


	private static final Log log = LogFactory.getLog(IAlarmService.class);

	@Resource
	public IAlarmDAO alarmDAO;

	public AlarmServiceImpl() {
		super();
	}

	@Override
	public Alarm saveNewAlarm(Alarm alarm)  throws Exception{
		try {
			String id = UuidUtil.getUuid();
			alarm.setUuid(id);
			alarmDAO.saveAlarm(alarm);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return alarm;
	}

	@Override
	public Alarm updateAlarm(Alarm alarm)  throws Exception{
		alarmDAO.updateAlarm(alarm);
		return alarm;
	}

	@Override
	public Alarm findAlarmById(java.lang.String uuid) throws Exception{
		try {
			return alarmDAO.getAlarm(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public void removeAlarm(String uuid) throws Exception{
		try {
		alarmDAO.removeAlarm(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception{
		try {
			List<Alarm> list = alarmDAO.queryList(pager);
			int totalCount = alarmDAO.selectCount(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

}