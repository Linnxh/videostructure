package com.sensing.core.bean;

import java.io.Serializable;

public class TaskChannelResp extends TaskChannel implements Serializable {
    private String channel_name;
    private Integer cap_stat;

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public Integer getCap_stat() {
        return cap_stat;
    }

    public void setCap_stat(Integer cap_stat) {
        this.cap_stat = cap_stat;
    }
}
