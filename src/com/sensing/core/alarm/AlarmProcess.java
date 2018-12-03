package com.sensing.core.alarm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sensing.core.dao.IAlarmInfoSaveDAO;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.UuidUtil;

/**
 * 
 * <p>Title: AlarmProcess</p>
 * <p>Description:告警流程</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月22日
 * @version 1.0
 */
public class AlarmProcess implements Runnable{
	
	private IAlarmInfoSaveDAO alarmInfoSaveDAO;
	
	public AlarmProcess(){}
	public AlarmProcess(IAlarmInfoSaveDAO alarmInfoSaveDAO){
		this.alarmInfoSaveDAO=alarmInfoSaveDAO;
	}
	
	private Log log = LogFactory.getLog(AlarmProcess.class);

	public void run(){
		try {
			while ( true ) {
				CmpResultBean cmpResultBean = AlarmTask.cmpResultQuene.poll(1,TimeUnit.SECONDS);
				
				if ( cmpResultBean == null ) {
					continue;
				}
				
				String threadName = Thread.currentThread().getName();
				if ( threadName.contains("thread-1") ) {
					long startTime = System.currentTimeMillis()/1000;
					if ( startTime % 20 == 0 ) {
						log.info("未处理的比对数据数量为:"+AlarmTask.cmpResultQuene.size());
					}
				}
				
				
				//查找该通道绑定的任务
				List<String> jobsList = AlarmCache.channelJobMap.get(cmpResultBean.getDeviceUuid());
				// 遍历任务
				if ( jobsList != null && jobsList.size() > 0 ) {
					for (int i = 0; i < jobsList.size() ; i++) {
						String jobsUuid = jobsList.get(i);
						// 判断任务和模板库的关联
						List<Integer> templateDbList  = AlarmCache.jobTemplateDbMap.get(jobsUuid);
						if( templateDbList == null || templateDbList.size() < 1 || !templateDbList.contains(cmpResultBean.getTemplateDbId())){
							continue;
						}
						// 判断任务是否在布控时间点
						JobBean job = AlarmCache.jobMap.get(jobsList.get(i));
						if ( !isJobWork(job, new Date(cmpResultBean.getCapTime()*1000)) ) {
							continue;
						}
						
						// 不满足告警的条件
						if ( cmpResultBean.getScore() != null ) {
							continue;
						}
						
						//满足告警
						//判断告警对象是否已有
						String alarmKey = cmpResultBean.getIdentityId()+jobsUuid+cmpResultBean.getTemplateObjUuid();
						AlarmInfo alarmInfo = AlarmCache.alarmInfoMap.get(alarmKey);
						if ( alarmInfo == null ) {
							alarmInfo = new AlarmInfo();
							alarmInfo.setUuid(UuidUtil.getUuid());
							alarmInfo.setAlarmTime(System.currentTimeMillis()/1000);
							alarmInfo.setJobsUuid(jobsUuid);
							//保存数据
							saveAlarm(alarmInfo, cmpResultBean);
							//更新缓存数据
							alarmInfo.setAlarmFlag(true);
							AlarmCache.alarmInfoMap.put(alarmKey,alarmInfo);
						}else{
							alarmInfo.setJobsUuid(jobsUuid);
							//保存数据
							saveAlarm(alarmInfo, cmpResultBean);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error("告警流程发生异常:"+e.getMessage());
		}
	}
	
	public void saveAlarm(AlarmInfo alarmInfo,CmpResultBean cmpResultBean){
		//保存抓拍信息
		try {
			Map<String,String> capParams = new HashMap<String, String>();
			capParams.put("uuid",cmpResultBean.getCapUuid());
			capParams.put("deviceId",cmpResultBean.getDeviceUuid());
			capParams.put("identityId",cmpResultBean.getIdentityId());
			capParams.put("capType",Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE+"");
			capParams.put("capTime",cmpResultBean.getCapTime()+"");
			capParams.put("capImgUrl",cmpResultBean.getCapImgUrl());
			// TODO 
			capParams.put("deviceArea","暂无该字段值");
			int i = alarmInfoSaveDAO.saveCapture(capParams);
			log.info("告警中抓拍入库成功.i="+i);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("告警中抓拍入库发生异常:"+e.getMessage());
		}

		//未告警的情况下，先保存数据
		//再告警的情况就不在更新数据
		if ( !alarmInfo.isAlarmFlag() ) {
			// 保存告警的数据
			try {
				Map<String, Object> alarmParams = new HashMap<String,Object>();
				alarmParams.put("uuid",alarmInfo.getUuid());
				alarmParams.put("deviceId",cmpResultBean.getDeviceUuid());
				alarmParams.put("alarmTime",alarmInfo.getAlarmTime());
				alarmParams.put("capTime",cmpResultBean.getCapTime());
				alarmParams.put("cmpUuid",null);
				alarmParams.put("cmpTemplatedbId",cmpResultBean.getTemplateDbId());
				alarmParams.put("cmpObjUuid",cmpResultBean.getTemplateObjUuid());
				alarmParams.put("cmpScore",null);
				alarmParams.put("capUuid",cmpResultBean.getCapUuid());
				alarmParams.put("jobUuid",alarmInfo.getJobsUuid());
				alarmParams.put("state",10);
				
				int i = alarmInfoSaveDAO.saveAlarm(alarmParams);
				log.info("告警中告警数据入库成功.i="+i);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("告警中告警入库发生异常:"+e.getMessage());
			}
		}
		
		//告警管理表添加
		try {
			Map<String, String> alarmCmpLogsParams = new HashMap<String, String>();
			alarmCmpLogsParams.put("uuid",UuidUtil.getUuid());
			alarmCmpLogsParams.put("alarmUuid",alarmInfo.getUuid());
			alarmCmpLogsParams.put("cmpUuid",null);
			alarmCmpLogsParams.put("capUuid",cmpResultBean.getCapUuid());
			alarmCmpLogsParams.put("sceneUrl",cmpResultBean.getSceneUrl());
			
			int i = alarmInfoSaveDAO.saveAlarmCmpLogs(alarmCmpLogsParams);
			log.info("告警中告警关联信息数据入库成功.i="+i);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("告警中告警关联信息入库发生异常:"+e.getMessage());
		}
		
	}
	
	/**
	 * 判断任务是否在布控时间点
	 * @param jobBean
	 * @param capDate
	 * @return
	 * @author mingxingyu
	 * @date   2018年11月28日 下午2:19:40
	 */
	public boolean isJobWork(JobBean jobBean,Date capDate){
		
		//星期数是否在布控范围内
		if ( !AlarmUtils.isRunWeek(jobBean.getRunWeek(), capDate) ){
			return false;
		}
		
		//日期是否在布控时间范围内
		if ( !AlarmUtils.isDateRange(jobBean.getBeginDate(), jobBean.getEndDate(), capDate) ){
			return false;
		}
		
		//时间段是否在布控的
		if ( !AlarmUtils.isTimeRange(jobBean.getBeginTime(), jobBean.getEndTime(), capDate) ) {
			return false;
		}
		
		return true;
	}
}
