package com.shkjs.doctor.bean;

import java.util.List;

/**
 * Created by Shuwen on 2016/10/8.
 */

public class HealthDcBean {
    private String healthDcName;
    private String healthDcCreateTime;
    private String healthDcContent;
    private String healthDcVoice;
    private String healthDcVoiceTime;
    private List<String> healthDcImgs;

    public String getHealthDcName() {
        return healthDcName;
    }

    public void setHealthDcName(String healthDcName) {
        this.healthDcName = healthDcName;
    }

    public String getHealthDcCreateTime() {
        return healthDcCreateTime;
    }

    public void setHealthDcCreateTime(String healthDcCreateTime) {
        this.healthDcCreateTime = healthDcCreateTime;
    }

    public String getHealthDcContent() {
        return healthDcContent;
    }

    public void setHealthDcContent(String healthDcContent) {
        this.healthDcContent = healthDcContent;
    }

    public String getHealthDcVoice() {
        return healthDcVoice;
    }

    public void setHealthDcVoice(String healthDcVoice) {
        this.healthDcVoice = healthDcVoice;
    }

    public String getHealthDcVoiceTime() {
        return healthDcVoiceTime;
    }

    public void setHealthDcVoiceTime(String healthDcVoiceTime) {
        this.healthDcVoiceTime = healthDcVoiceTime;
    }

    public List<String> getHealthDcImgs() {
        return healthDcImgs;
    }

    public void setHealthDcImgs(List<String> healthDcImgs) {
        this.healthDcImgs = healthDcImgs;
    }

}
