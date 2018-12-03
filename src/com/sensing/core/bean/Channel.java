package com.sensing.core.bean;

import java.io.Serializable;
import java.util.List;

import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ExcelVOAttribute;
/**
 *@author wenbo
 */
@SuppressWarnings("all")
public class Channel extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String uuid;
	@ExcelVOAttribute(name = "通道名称", column = "A", isExport = true)
	private String channelName;
	private String channelDescription;
	private Integer channelType;
	private String channelAddr;
	private String channelRtmp;//视频rtmp流地址
	private Integer channelPort;
	private String channelUid;
	private String channelPsw;
	private String channelNo;
	private String channelGuid;
	private Integer minFaceWidth;
	private Integer minImgQuality;
	private Integer minCapDistance;
	private Integer maxSaveDistance;
	private Integer maxYaw;
	private Integer maxRoll;
	private Integer maxPitch;
	private Double channelLongitude;
	private Double channelLatitude;
	private String channelArea;
	private Integer channelDirect;
	private Double channelThreshold;
	private Integer capStat;
	private Short isDel;
	private java.util.Date lastTimestamp;
	private String lastTimestampStr;
	private Integer regionId;
	private String regionName;
	private Integer reserved;
	private String sdkVer;
	private Short important;
	private String channelVerid;
	private String searchCode;
	private String channelState;//通道状态
	
	private String nodeType;//区域或通道
	private String parentId;
	
	private String orgName;

	private List<Channel> childChannels;
	
	private String uid;//创建用户的uuid
	private Integer type;//通道类型，视频平台0；rtsp1
	private Integer sipNetType;//通讯类型
	
	private Long createTime;//创建时间
	private Long modifyTime;//修改时间
	private String createUserName;
	
	public String getChannelRtmp() {
		return channelRtmp;
	}
	public void setChannelRtmp(String channelRtmp) {
		this.channelRtmp = channelRtmp;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}
	public Integer getSipNetType() {
		return sipNetType;
	}
	public void setSipNetType(Integer sipNetType) {
		this.sipNetType = sipNetType;
	}
	public Integer getType() {
		if (regionName!=null && regionName.equals("手机通道")) {
			return Constants.CHANNEL_TYPE_RTSP;
		}else {
			return Constants.CHANNEL_TYPE_VIDEO;
		}
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	private String capStatTag;	//抓拍状态显示

	public String getUuid(){
		return uuid;
	}
	public void setUuid(String uuid){
		this.uuid=uuid;
	}
	public String getChannelName(){
		return channelName;
	}
	public void setChannelName(String channelName){
		this.channelName=channelName;
	}
	public String getChannelDescription(){
		return channelDescription;
	}
	public void setChannelDescription(String channelDescription){
		this.channelDescription=channelDescription;
	}
	public Integer getChannelType(){
		return channelType;
	}
	public void setChannelType(Integer channelType){
		this.channelType=channelType;
	}
	public String getChannelAddr(){
		return channelAddr;
	}
	public void setChannelAddr(String channelAddr){
		this.channelAddr=channelAddr;
	}
	public Integer getChannelPort(){
		return channelPort;
	}
	public void setChannelPort(Integer channelPort){
		this.channelPort=channelPort;
	}
	public String getChannelUid(){
		return channelUid;
	}
	public void setChannelUid(String channelUid){
		this.channelUid=channelUid;
	}
	public String getChannelPsw(){
		return channelPsw;
	}
	public void setChannelPsw(String channelPsw){
		this.channelPsw=channelPsw;
	}
	public String getChannelNo(){
		return channelNo;
	}
	public void setChannelNo(String channelNo){
		this.channelNo=channelNo;
	}
	public String getChannelGuid(){
		return channelGuid;
	}
	public void setChannelGuid(String channelGuid){
		this.channelGuid=channelGuid;
	}
	public Integer getMinFaceWidth(){
		return minFaceWidth;
	}
	public void setMinFaceWidth(Integer minFaceWidth){
		this.minFaceWidth=minFaceWidth;
	}
	public Integer getMinImgQuality(){
		return minImgQuality;
	}
	public void setMinImgQuality(Integer minImgQuality){
		this.minImgQuality=minImgQuality;
	}
	public Integer getMinCapDistance(){
		return minCapDistance;
	}
	public void setMinCapDistance(Integer minCapDistance){
		this.minCapDistance=minCapDistance;
	}
	public Integer getMaxSaveDistance(){
		return maxSaveDistance;
	}
	public void setMaxSaveDistance(Integer maxSaveDistance){
		this.maxSaveDistance=maxSaveDistance;
	}
	public Integer getMaxYaw(){
		return maxYaw;
	}
	public void setMaxYaw(Integer maxYaw){
		this.maxYaw=maxYaw;
	}
	public Integer getMaxRoll(){
		return maxRoll;
	}
	public void setMaxRoll(Integer maxRoll){
		this.maxRoll=maxRoll;
	}
	public Integer getMaxPitch(){
		return maxPitch;
	}
	public void setMaxPitch(Integer maxPitch){
		this.maxPitch=maxPitch;
	}
	public Double getChannelLongitude(){
		return channelLongitude;
	}
	public void setChannelLongitude(Double channelLongitude){
		this.channelLongitude=channelLongitude;
	}
	public Double getChannelLatitude(){
		return channelLatitude;
	}
	public void setChannelLatitude(Double channelLatitude){
		this.channelLatitude=channelLatitude;
	}
	public String getChannelArea(){
		return channelArea;
	}
	public void setChannelArea(String channelArea){
		this.channelArea=channelArea;
	}
	public Integer getChannelDirect(){
		return channelDirect;
	}
	public void setChannelDirect(Integer channelDirect){
		this.channelDirect=channelDirect;
	}
	public Double getChannelThreshold(){
		return channelThreshold;
	}
	public void setChannelThreshold(Double channelThreshold){
		this.channelThreshold=channelThreshold;
	}
	public Integer getCapStat(){
		return capStat;
	}
	public void setCapStat(Integer capStat){
		this.capStat=capStat;
	}
	public Short getIsDel(){
		return isDel;
	}
	public void setIsDel(Short isDel){
		this.isDel=isDel;
	}
	public java.util.Date getLastTimestamp(){
		return lastTimestamp;
	}
	public void setLastTimestamp(java.util.Date lastTimestamp){
		this.lastTimestamp=lastTimestamp;
	}
	public String getLastTimestampStr(){
		if(lastTimestamp!=null){
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(lastTimestamp);
		}else{
			return "";
		}
	}
	public void setLastTimestampStr(String lastTimestampStr) throws Exception{
		if(lastTimestampStr!=null&&!lastTimestampStr.trim().equals("")){
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.lastTimestamp = sdf.parse(lastTimestampStr);
		}else
			this.lastTimestamp = null;
	}
	public Integer getRegionId(){
		return regionId;
	}
	public void setRegionId(Integer regionId){
		this.regionId=regionId;
	}
	public Integer getReserved(){
		return reserved;
	}
	public void setReserved(Integer reserved){
		this.reserved=reserved;
	}
	public String getSdkVer(){
		return sdkVer;
	}
	public void setSdkVer(String sdkVer){
		this.sdkVer=sdkVer;
	}
	public Short getImportant(){
		return important;
	}
	public void setImportant(Short important){
		this.important=important;
	}
	public String getChannelVerid(){
		return channelVerid;
	}
	public void setChannelVerid(String channelVerid){
		this.channelVerid=channelVerid;
	}
	public String getCapStatTag() {
		return Constants.CAP_STAT_MAP.get(capStat);
	}
	public void setCapStatTag(String capStatTag) {
		this.capStatTag = capStatTag;
	}
	public String getSearchCode() {
		return searchCode;
	}
	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}
	public List<Channel> getChildChannels() {
		return childChannels;
	}
	public void setChildChannels(List<Channel> childChannels) {
		this.childChannels = childChannels;
	}
	public String getChannelState() {
		return channelState;
	}
	public void setChannelState(String channelState) {
		this.channelState = channelState;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	@Override
	public String toString() {
		return "Channel [uuid=" + uuid + ", channelName=" + channelName
				+ ", channelDescription=" + channelDescription
				+ ", channelType=" + channelType + ", channelAddr="
				+ channelAddr + ", channelPort=" + channelPort
				+ ", channelUid=" + channelUid + ", channelPsw=" + channelPsw
				+ ", channelNo=" + channelNo + ", channelGuid=" + channelGuid
				+ ", minFaceWidth=" + minFaceWidth + ", minImgQuality="
				+ minImgQuality + ", minCapDistance=" + minCapDistance
				+ ", maxSaveDistance=" + maxSaveDistance + ", maxYaw=" + maxYaw
				+ ", maxRoll=" + maxRoll + ", maxPitch=" + maxPitch
				+ ", channelLongitude=" + channelLongitude
				+ ", channelLatitude=" + channelLatitude + ", channelArea="
				+ channelArea + ", channelDirect=" + channelDirect
				+ ", channelThreshold=" + channelThreshold + ", capStat="
				+ capStat + ", isDel=" + isDel + ", lastTimestamp="
				+ lastTimestamp + ", lastTimestampStr=" + lastTimestampStr
				+ ", regionId=" + regionId + ", regionName=" + regionName
				+ ", reserved=" + reserved + ", sdkVer=" + sdkVer
				+ ", important=" + important + ", channelVerid=" + channelVerid
				+ ", searchCode=" + searchCode + ", channelState="
				+ channelState + ", nodeType=" + nodeType + ", parentId="
				+ parentId + ", childChannels=" + childChannels
				+ ", capStatTag=" + capStatTag + "]";
	}
	
}