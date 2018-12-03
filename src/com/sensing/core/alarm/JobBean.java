package com.sensing.core.alarm;

import java.util.Date;

/**
 * 
 * <p>Title: JobBean</p>
 * <p>Description:布控任务对象</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月28日
 * @version 1.0
 */
public class JobBean {

	private String uuid;//任务的uuid
	private Integer state;//任务的状态
	private String beginTime;//任务的开始时间
	private String endTime;//任务的结束时间
	private Date beginDate;//任务的开始日期
	private Date endDate;//任务的结束日期
	private Double score;//比对分值
	private String runWeek;//布控的周几
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public String getRunWeek() {
		return runWeek;
	}
	public void setRunWeek(String runWeek) {
		this.runWeek = runWeek;
	}
}
