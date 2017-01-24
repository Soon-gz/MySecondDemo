package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/25.
 */

public class PatientChartBean {
    private String pictrueConPhoto;
    private String pictrueConName;
    private String pictrueConTime;
    private String pictrueConContent;
    private String pictrueConIsFinish;
    private String pictrueConPhoneNum;
    private String pictrueConUnreadNum;

    public String getPictrueConUnreadNum() {
        return pictrueConUnreadNum;
    }

    public void setPictrueConUnreadNum(String pictrueConUnreadNum) {
        this.pictrueConUnreadNum = pictrueConUnreadNum;
    }

    public String getPictrueConPhoneNum() {
        return pictrueConPhoneNum;
    }

    public void setPictrueConPhoneNum(String pictrueConPhoneNum) {
        this.pictrueConPhoneNum = pictrueConPhoneNum;
    }

    public String getPictrueConPhoto() {
        return pictrueConPhoto;
    }

    public void setPictrueConPhoto(String pictrueConPhoto) {
        this.pictrueConPhoto = pictrueConPhoto;
    }

    public String getPictrueConName() {
        return pictrueConName;
    }

    public void setPictrueConName(String pictrueConName) {
        this.pictrueConName = pictrueConName;
    }

    public String getPictrueConTime() {
        return pictrueConTime;
    }

    public void setPictrueConTime(String pictrueConTime) {
        this.pictrueConTime = pictrueConTime;
    }

    public String getPictrueConContent() {
        return pictrueConContent;
    }

    public void setPictrueConContent(String pictrueConContent) {
        this.pictrueConContent = pictrueConContent;
    }

    public String getPictrueConIsFinish() {
        return pictrueConIsFinish;
    }

    public void setPictrueConIsFinish(String pictrueConIsFinish) {
        this.pictrueConIsFinish = pictrueConIsFinish;
    }

    @Override
    public String toString() {
        return "PatientChartBean{" +
                "pictrueConPhoto='" + pictrueConPhoto + '\'' +
                ", pictrueConName='" + pictrueConName + '\'' +
                ", pictrueConTime='" + pictrueConTime + '\'' +
                ", pictrueConContent='" + pictrueConContent + '\'' +
                ", pictrueConIsFinish='" + pictrueConIsFinish + '\'' +
                '}';
    }
}
