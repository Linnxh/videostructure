package com.sensing.core.alarm;

public class CapBean {
	
	private String capUuid;//抓拍的uuid
	private String deviceUuid;//抓拍所在通道的uuid
	private String identityId;//抓拍对象的唯一标识
	private Long capTime;//抓拍时间
	private String capImgUrl;//抓拍图存储路径
	private String capSceneUrl;//抓拍场景图存储路径
	private byte[] fea;//特征文件
	private String feaBase64;//特征文件的base64字符串
	
	private String plateNo;//车牌号码
	private Integer plateColor;//车牌颜色
	private String vehicleBrandTag;//品牌
	private String vehicleModelTag;//子品牌
	private String vehicleStylesTag;//年款
	private Integer vehicleColor;//车身颜色
	private Integer vehicleClass;//车辆类型
	
	
	public byte[] getFea() {
		return fea;
	}
	public void setFea(byte[] fea) {
		this.fea = fea;
	}
	public String getFeaBase64() {
		return feaBase64;
	}
	public void setFeaBase64(String feaBase64) {
		this.feaBase64 = feaBase64;
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
	public String getCapSceneUrl() {
		return capSceneUrl;
	}
	public void setCapSceneUrl(String capSceneUrl) {
		this.capSceneUrl = capSceneUrl;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}
	public Integer getPlateColor() {
		return plateColor;
	}
	public void setPlateColor(Integer plateColor) {
		this.plateColor = plateColor;
	}
	public String getVehicleBrandTag() {
		return vehicleBrandTag;
	}
	public void setVehicleBrandTag(String vehicleBrandTag) {
		this.vehicleBrandTag = vehicleBrandTag;
	}
	public String getVehicleModelTag() {
		return vehicleModelTag;
	}
	public void setVehicleModelTag(String vehicleModelTag) {
		this.vehicleModelTag = vehicleModelTag;
	}
	public String getVehicleStylesTag() {
		return vehicleStylesTag;
	}
	public void setVehicleStylesTag(String vehicleStylesTag) {
		this.vehicleStylesTag = vehicleStylesTag;
	}
	public Integer getVehicleColor() {
		return vehicleColor;
	}
	public void setVehicleColor(Integer vehicleColor) {
		this.vehicleColor = vehicleColor;
	}
	public Integer getVehicleClass() {
		return vehicleClass;
	}
	public void setVehicleClass(Integer vehicleClass) {
		this.vehicleClass = vehicleClass;
	}
}
