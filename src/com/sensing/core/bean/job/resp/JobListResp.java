package com.sensing.core.bean.job.resp;

import com.sensing.core.utils.Constants;

import java.io.Serializable;
import java.util.Date;

public class JobListResp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String jobName;
    //布控人
    private String createUsername;
    //布控状态 10:待启动 20:布控中  30:暂停中 40:休息中 50:已撤控  60:撤控中 70:已完成
    private Integer state;
    private String stateStr;
    private Integer ratifyResult;
    private Integer isDeleted;
    private Integer level;
    private String levelStr;
    //阈值
    private Integer score;
    //布控人
    private String channelname;
    private String templatename;
    //报警次数
    private Integer alarmCount;
    //布控内容
    private String jobContent;

    private String runWeek;
    private String beginTime;
    private String endTime;

    private Date beginDate;
    private String beginDateStr;
    private Date endDate;
    private String endDateStr;
    //是否订阅
    private Integer subscribe;


    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public String getStateStr() {
        return Constants.JOB_STATE_MAP.get(state);
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }

    public String getEndDateStr() {
        if (endDate != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(endDate);
        } else {
            return "";
        }
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }


    public String getBeginDateStr() {
        if (beginDate != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(beginDate);
        } else {
            return "";
        }
    }

    public void setBeginDateStr(String beginDateStr) {
        this.beginDateStr = beginDateStr;
    }


    public String getRunWeek() {
        return runWeek;
    }

    public void setRunWeek(String runWeek) {
        this.runWeek = runWeek;
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

    public String getJobContent() {
        return jobContent;
    }

    public void setJobContent(String jobContent) {
        this.jobContent = jobContent;
    }

    public String getLevelStr() {
        if (level == null || level.intValue() == 1) {
            return "一级";
        } else if (level.intValue() == 2) {
            return "二级";
        } else if (level.intValue() == 3) {
            return "三级";
        } else {
            return "未知";
        }
    }

    public void setLevelStr(String levelStr) {
        this.levelStr = levelStr;
    }

    public Integer getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(Integer alarmCount) {
        this.alarmCount = alarmCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getRatifyResult() {
        return ratifyResult;
    }

    public void setRatifyResult(Integer ratifyResult) {
        this.ratifyResult = ratifyResult;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }
}
