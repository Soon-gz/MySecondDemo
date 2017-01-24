package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/25.
 */

public class HistoryOrderBean {
    private String historyOdId;
    private String historyOdTime;
    private String historyOdPhoto;
    private String historyOdName;
    private String historyOdMoney;

    public String getHistoryOdId() {
        return historyOdId;
    }

    public void setHistoryOdId(String historyOdId) {
        this.historyOdId = historyOdId;
    }

    public String getHistoryOdTime() {
        return historyOdTime;
    }

    public void setHistoryOdTime(String historyOdTime) {
        this.historyOdTime = historyOdTime;
    }

    public String getHistoryOdPhoto() {
        return historyOdPhoto;
    }

    public void setHistoryOdPhoto(String historyOdPhoto) {
        this.historyOdPhoto = historyOdPhoto;
    }

    public String getHistoryOdName() {
        return historyOdName;
    }

    public void setHistoryOdName(String historyOdName) {
        this.historyOdName = historyOdName;
    }

    public String getHistoryOdMoney() {
        return historyOdMoney;
    }

    public void setHistoryOdMoney(String historyOdMoney) {
        this.historyOdMoney = historyOdMoney;
    }



    @Override
    public String toString() {
        return "HistoryOrderBean{" +
                "historyOdId='" + historyOdId + '\'' +
                ", historyOdTime='" + historyOdTime + '\'' +
                ", historyOdPhoto='" + historyOdPhoto + '\'' +
                ", historyOdName='" + historyOdName + '\'' +
                ", historyOdMoney='" + historyOdMoney + '\'' +
                '}';
    }
}
