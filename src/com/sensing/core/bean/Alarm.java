package com.sensing.core.bean;

import java.io.Serializable;
/**
 *@author mingxingyu
 */
public class Alarm implements Serializable{
	private static final long serialVersionUID = 1L;
	private String uuid;
	private String deviceId;
	private Long alarmTime;
	private Long capTime;
	private String capUuid;
	private String cmpUuid;
	private Integer cmpTemplatedbId;
	private String cmpObjUuid;
	private Double cmpScore;
	private String jobUuid;

	public String getUuid(){
		return uuid;
	}
	public void setUuid(String uuid){
		this.uuid=uuid;
	}
	public String getDeviceId(){
		return deviceId;
	}
	public void setDeviceId(String deviceId){
		this.deviceId=deviceId;
	}
	public Long getAlarmTime(){
		return alarmTime;
	}
	public void setAlarmTime(Long alarmTime){
		this.alarmTime=alarmTime;
	}
	public Long getCapTime(){
		return capTime;
	}
	public void setCapTime(Long capTime){
		this.capTime=capTime;
	}
	public String getCapUuid(){
		return capUuid;
	}
	public void setCapUuid(String capUuid){
		this.capUuid=capUuid;
	}
	public String getCmpUuid(){
		return cmpUuid;
	}
	public void setCmpUuid(String cmpUuid){
		this.cmpUuid=cmpUuid;
	}
	public Integer getCmpTemplatedbId(){
		return cmpTemplatedbId;
	}
	public void setCmpTemplatedbId(Integer cmpTemplatedbId){
		this.cmpTemplatedbId=cmpTemplatedbId;
	}
	public String getCmpObjUuid(){
		return cmpObjUuid;
	}
	public void setCmpObjUuid(String cmpObjUuid){
		this.cmpObjUuid=cmpObjUuid;
	}
	public Double getCmpScore(){
		return cmpScore;
	}
	public void setCmpScore(Double cmpScore){
		this.cmpScore=cmpScore;
	}
	public String getJobUuid(){
		return jobUuid;
	}
	public void setJobUuid(String jobUuid){
		this.jobUuid=jobUuid;
	}
}