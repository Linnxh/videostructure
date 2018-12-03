package com.sensing.core.bean;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.sensing.core.thrifts.cap.bean.CapPeopleResult;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.MatchUtil;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.TransfTimeUtil;

/**
 * @author wenbo
 */
public class Person implements Serializable {
//	private static final long serialVersionUID = 1L;此处去掉serialVersionUID，防止mongo查询时字段转换实体类把UID当作检索字段查询
	private String uuid;
	private Integer type;
//	private String channelUuid;
	private String deviceId;
	private Long capTime;
	private String capTimeStr;
	private Integer frameTime;
	private String frameTimeStr;
	private Integer age;
	private String ageTag;
	private Integer genderCode;
	private String genderCodeTag;
//	private Integer carryThingsBag;
	private Integer bagStyle;
	private String bagStyleTag;
//	private Integer carryThingsPortable;
	private Integer bigBagStyle;
	private String bigBagStyleTag;
	private Integer orientation;
	private String orientationTag;
//	private Integer moveState;
	private Integer motion;
	private String motionTag;
//	private Integer hat;
	private Integer cap;
	private String capTag;
//	private Integer mask;
	private Integer respirator;
	private String respiratorTag;
	private Integer glass;
	private String glassTag;
//	private Integer upperClothesColor;
	private Integer coatColor;
	private String coatColorTag;
//	private Integer upperClothesType;
	private Integer coatLength;
	private String coatLengthTag;
//	private Integer upperClothesTexture;
	private Integer coatTexture;
	private String coatTextureTag;
//	private Integer lowerClothesColor;
	private Integer trousersColor;
	private String trousersColorTag;
//	private Integer lowerClothesType;
	private Integer trousersLen;
	private String trousersLenTag;
//	private Integer lowerClothesTexture;
	private Integer trousersTexture;
	private String trousersTextureTag;
	private String capUrl;
	private String seceneUrl;
	private String videoUrl;
	private Integer isDeleted;
	private String createUser;
	private java.util.Date createTime;
	private String createTimeStr;
	private String modifyUser;
	private java.util.Date modifyTime;
	private String modifyTimeStr;
	private String capLocation;
	private Integer capType;
	private String capFeature;
	private Float score;

	public Person() {
	}

	public Person(CapPeopleResult capPeopleResult) {
		this.age = capPeopleResult.getAge();
		this.capLocation = capPeopleResult.getCapLocation();
		this.capType = capPeopleResult.getCapType();
		this.bagStyle = capPeopleResult.getCarryThingsBag();
		this.bigBagStyle = capPeopleResult.getCarryThingsPortable();
		this.glass = capPeopleResult.getGlass();
		this.cap = capPeopleResult.getHat();
		this.trousersColor = capPeopleResult.getLowerClothesColor();
		this.trousersTexture = capPeopleResult.getLowerClothesTexture();
		this.trousersLen = capPeopleResult.getLowerClothesType();
		this.respirator = capPeopleResult.getMask();
		this.motion = capPeopleResult.getMoveState();
		this.orientation = capPeopleResult.getOrientation();
		this.genderCode = capPeopleResult.getSex();
		this.coatColor = capPeopleResult.getUpperClothesColor();
		this.coatTexture = capPeopleResult.getUpperClothesTexture();
		this.coatLength = capPeopleResult.getUpperClothesType();
		if (capPeopleResult.getCapFeature() != null && capPeopleResult.getCapFeature().length > 0) {
			this.capFeature = StringUtils.byte2Base64StringFun(capPeopleResult.getCapFeature());
		}
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getCapFeature() {
		return capFeature;
	}

	public void setCapFeature(String capFeature) {
		this.capFeature = capFeature;
	}

	public String getSeceneUrl() {
		return seceneUrl;
	}

	public void setSeceneUrl(String seceneUrl) {
		this.seceneUrl = seceneUrl;
	}

	public Integer getCapType() {
		return capType;
	}

	public void setCapType(Integer capType) {
		this.capType = capType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(Integer genderCode) {
		this.genderCode = genderCode;
	}

	public Integer getBagStyle() {
		return bagStyle;
	}

	public void setBagStyle(Integer bagStyle) {
		this.bagStyle = bagStyle;
	}

	public Integer getBigBagStyle() {
		return bigBagStyle;
	}

	public void setBigBagStyle(Integer bigBagStyle) {
		this.bigBagStyle = bigBagStyle;
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}

	public Integer getMotion() {
		return motion;
	}

	public void setMotion(Integer motion) {
		this.motion = motion;
	}

	public Integer getCap() {
		return cap;
	}

	public void setCap(Integer cap) {
		this.cap = cap;
	}

	public Integer getRespirator() {
		return respirator;
	}

	public void setRespirator(Integer respirator) {
		this.respirator = respirator;
	}

	public Integer getGlass() {
		return glass;
	}

	public void setGlass(Integer glass) {
		this.glass = glass;
	}

	public Integer getCoatColor() {
		return coatColor;
	}

	public void setCoatColor(Integer coatColor) {
		this.coatColor = coatColor;
	}

	public Integer getCoatLength() {
		return coatLength;
	}

	public void setCoatLength(Integer coatLength) {
		this.coatLength = coatLength;
	}

	public Integer getCoatTexture() {
		return coatTexture;
	}

	public void setCoatTexture(Integer coatTexture) {
		this.coatTexture = coatTexture;
	}

	public Integer getTrousersColor() {
		return trousersColor;
	}

	public void setTrousersColor(Integer trousersColor) {
		this.trousersColor = trousersColor;
	}

	public Integer getTrousersLen() {
		return trousersLen;
	}

	public void setTrousersLen(Integer trousersLen) {
		this.trousersLen = trousersLen;
	}

	public Integer getTrousersTexture() {
		return trousersTexture;
	}

	public void setTrousersTexture(Integer trousersTexture) {
		this.trousersTexture = trousersTexture;
	}

	public String getCapUrl() {
		return capUrl;
	}

	public void setCapUrl(String capUrl) {
		this.capUrl = capUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		if (createTime != null) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(createTime);
		} else {
			return "";
		}
	}

	public void setCreateTimeStr(String createTimeStr) throws Exception {
		if (createTimeStr != null && !createTimeStr.trim().equals("")) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.createTime = sdf.parse(createTimeStr);
		} else
			this.createTime = null;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public java.util.Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(java.util.Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyTimeStr() {
		if (modifyTime != null) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(modifyTime);
		} else {
			return "";
		}
	}

	public void setModifyTimeStr(String modifyTimeStr) throws Exception {
		if (modifyTimeStr != null && !modifyTimeStr.trim().equals("")) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.modifyTime = sdf.parse(modifyTimeStr);
		} else
			this.modifyTime = null;
	}

	public Long getCapTime() {
		return capTime;
	}

	public void setCapTime(Long capTime) {
		this.capTime = capTime;
	}

	public String getCapTimeStr() {
		if (capTime != null && MatchUtil.isTenPositive(capTime.toString())) {
			String formatCapTime = TransfTimeUtil.UnixTimeStamp2Date(capTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
			return formatCapTime;
		} else {
			return "";
		}
	}

	public void setCapTimeStr(String capTimeStr) {
		this.capTimeStr = capTimeStr;
	}

	public Integer getFrameTime() {
		return frameTime;
	}

	public void setFrameTime(Integer frameTime) {
		this.frameTime = frameTime;
	}

	public String getFrameTimeStr() {
		if (frameTime != null) {
			String frameTimeStr = TransfTimeUtil.getHMS(frameTime);
			return frameTimeStr;
		} else {
			return "";
		}
	}

	public void setFrameTimeStr(String frameTimeStr) {
		this.frameTimeStr = frameTimeStr;
	}

	public String getAgeTag() {
		return ageTag;
	}

	public void setAgeTag(String ageTag) {
		this.ageTag = ageTag;
	}

	public String getGenderCodeTag() {
		return genderCodeTag;
	}

	public void setGenderCodeTag(String genderCodeTag) {
		this.genderCodeTag = genderCodeTag;
	}

	public String getBagStyleTag() {
		return bagStyleTag;
	}

	public void setBagStyleTag(String bagStyleTag) {
		this.bagStyleTag = bagStyleTag;
	}

	public String getBigBagStyleTag() {
		return bigBagStyleTag;
	}

	public void setBigBagStyleTag(String bigBagStyleTag) {
		this.bigBagStyleTag = bigBagStyleTag;
	}

	public String getOrientationTag() {
		return orientationTag;
	}

	public void setOrientationTag(String orientationTag) {
		this.orientationTag = orientationTag;
	}

	public String getMotionTag() {
		return motionTag;
	}

	public void setMotionTag(String motionTag) {
		this.motionTag = motionTag;
	}

	public String getCapTag() {
		return capTag;
	}

	public void setCapTag(String capTag) {
		this.capTag = capTag;
	}

	public String getRespiratorTag() {
		return respiratorTag;
	}

	public void setRespiratorTag(String respiratorTag) {
		this.respiratorTag = respiratorTag;
	}

	public String getGlassTag() {
		return glassTag;
	}

	public void setGlassTag(String glassTag) {
		this.glassTag = glassTag;
	}

	public String getCoatColorTag() {
		return coatColorTag;
	}

	public void setCoatColorTag(String coatColorTag) {
		this.coatColorTag = coatColorTag;
	}

	public String getCoatLengthTag() {
		return coatLengthTag;
	}

	public void setCoatLengthTag(String coatLengthTag) {
		this.coatLengthTag = coatLengthTag;
	}

	public String getCoatTextureTag() {
		return coatTextureTag;
	}

	public void setCoatTextureTag(String coatTextureTag) {
		this.coatTextureTag = coatTextureTag;
	}

	public String getTrousersColorTag() {
		return trousersColorTag;
	}

	public void setTrousersColorTag(String trousersColorTag) {
		this.trousersColorTag = trousersColorTag;
	}

	public String getTrousersLenTag() {
		return trousersLenTag;
	}

	public void setTrousersLenTag(String trousersLenTag) {
		this.trousersLenTag = trousersLenTag;
	}

	public String getTrousersTextureTag() {
		return trousersTextureTag;
	}

	public void setTrousersTextureTag(String trousersTextureTag) {
		this.trousersTextureTag = trousersTextureTag;
	}

	public String getCapLocation() {
		return capLocation;
	}

	public void setCapLocation(String capLocation) {
		this.capLocation = capLocation;
	}

}