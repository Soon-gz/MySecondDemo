package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/12/8.
 * <p>
 * 红点显示
 */

public class UnreadMessage implements Serializable {

    private int sitDiagnoseReserveDotCount;
    private int groupSitDiagnoseDotCount;
    private int unReadMessageDotCount;
    private int inquiryReserveDotCount;
    private int healthReportCount;

    public int getSitDiagnoseReserveDotCount() {
        return sitDiagnoseReserveDotCount;
    }

    public void setSitDiagnoseReserveDotCount(int sitDiagnoseReserveDotCount) {
        this.sitDiagnoseReserveDotCount = sitDiagnoseReserveDotCount;
    }

    public int getGroupSitDiagnoseDotCount() {
        return groupSitDiagnoseDotCount;
    }

    public void setGroupSitDiagnoseDotCount(int groupSitDiagnoseDotCount) {
        this.groupSitDiagnoseDotCount = groupSitDiagnoseDotCount;
    }

    public int getUnReadMessageDotCount() {
        return unReadMessageDotCount;
    }

    public void setUnReadMessageDotCount(int unReadMessageDotCount) {
        this.unReadMessageDotCount = unReadMessageDotCount;
    }

    public int getInquiryReserveDotCount() {
        return inquiryReserveDotCount;
    }

    public void setInquiryReserveDotCount(int inquiryReserveDotCount) {
        this.inquiryReserveDotCount = inquiryReserveDotCount;
    }

    public int getHealthReportCount() {
        return healthReportCount;
    }

    public void setHealthReportCount(int healthReportCount) {
        this.healthReportCount = healthReportCount;
    }

    @Override
    public String toString() {
        return "UnreadMessage{" +
                "sitDiagnoseReserveDotCount=" + sitDiagnoseReserveDotCount +
                ", groupSitDiagnoseDotCount=" + groupSitDiagnoseDotCount +
                ", unReadMessageDotCount=" + unReadMessageDotCount +
                ", inquiryReserveDotCount=" + inquiryReserveDotCount +
                ", healthReportCount=" + healthReportCount +
                '}';
    }
}
