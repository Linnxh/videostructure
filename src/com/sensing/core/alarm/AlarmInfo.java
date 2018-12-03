package com.sensing.core.alarm;


/**
 * 
 * <p>Title: AlarmInfo</p>
 * <p>Description:告警对象</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月19日
 * @version 1.0
 */
public class AlarmInfo {
	
	//告警的uuid
	private String uuid;
	//已保存的告警的分数
	private Double highestsScore;
	// 告警时间
	private Long alarmTime;
	// 任务的uuid
	private String jobsUuid;
	//是否告警的标记
	private boolean alarmFlag = false;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Double getHighestsScore() {
		return highestsScore;
	}
	public void setHighestsScore(Double highestsScore) {
		this.highestsScore = highestsScore;
	}
	public Long getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(Long alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getJobsUuid() {
		return jobsUuid;
	}
	public void setJobsUuid(String jobsUuid) {
		this.jobsUuid = jobsUuid;
	}
	public boolean isAlarmFlag() {
		return alarmFlag;
	}
	public void setAlarmFlag(boolean alarmFlag) {
		this.alarmFlag = alarmFlag;
	}
}
