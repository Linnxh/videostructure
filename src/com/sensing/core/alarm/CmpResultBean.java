package com.sensing.core.alarm;

import java.util.Date;

/**
 * 比对结果封装对象
 * <p>Title: CmpResultBean</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月22日
 * @version 1.0
 */
public class CmpResultBean {

	private String uuid;//比对的uuid
	private String templateUuid;//命中的模板的uuid
	private Integer templateDbId;//命中的模板所属的模板库的id
	private String templateObjUuid;//命中的目标人的uuid
	private Double score;//比对的分值
	private Date cmpDate;//比对时间
	
	private String capUuid;//抓拍uuid
	private String deviceUuid;//通道的uuid
	private String identityId;//抓拍的唯一标识
	private Long capTime;//抓拍时间
	private String capImgUrl;//抓拍图路径
	private String sceneUrl;//场景图路径
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTemplateUuid() {
		return templateUuid;
	}
	public void setTemplateUuid(String templateUuid) {
		this.templateUuid = templateUuid;
	}
	public Integer getTemplateDbId() {
		return templateDbId;
	}
	public void setTemplateDbId(Integer templateDbId) {
		this.templateDbId = templateDbId;
	}
	public String getTemplateObjUuid() {
		return templateObjUuid;
	}
	public void setTemplateObjUuid(String templateObjUuid) {
		this.templateObjUuid = templateObjUuid;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Date getCmpDate() {
		return cmpDate;
	}
	public void setCmpDate(Date cmpDate) {
		this.cmpDate = cmpDate;
	}
	public String getCapUuid() {
		return capUuid;
	}
	public void setCapUuid(String capUuid) {
		this.capUuid = capUuid;
	}
	public String getDeviceUuid() {
		return deviceUuid;
	}
	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}
	public String getIdentityId() {
		return identityId;
	}
	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}
	public Long getCapTime() {
		return capTime;
	}
	public void setCapTime(Long capTime) {
		this.capTime = capTime;
	}
	public String getCapImgUrl() {
		return capImgUrl;
	}
	public void setCapImgUrl(String capImgUrl) {
		this.capImgUrl = capImgUrl;
	}
	public String getSceneUrl() {
		return sceneUrl;
	}
	public void setSceneUrl(String sceneUrl) {
		this.sceneUrl = sceneUrl;
	}
	
	public String toString() {
		return "CmpResultBean [uuid=" + uuid + ", templateUuid=" + templateUuid
				+ ", templateDbId=" + templateDbId + ", templateObjUuid="
				+ templateObjUuid + ", score=" + score + ", cmpDate=" + cmpDate
				+ ", capUuid=" + capUuid + ", deviceUuid=" + deviceUuid
				+ ", identityId=" + identityId + ", capTime=" + capTime
				+ ", capImgUrl=" + capImgUrl + ", sceneUrl=" + sceneUrl + "]";
	}
	
}
