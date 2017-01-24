package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class SitDiagnose extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -1154398030203326613L;

    private Long id;

    private int startTime;

    private int endTime;

    private int segmentType;

    private String diagnoseDate;

    private Integer diagnoseNum;

    private Integer diagnoseNumSubscribed;

    private Long doctorId;

    private Integer version;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getDiagnoseDate() {
        return diagnoseDate;
    }

    public void setDiagnoseDate(String diagnoseDate) {
        this.diagnoseDate = diagnoseDate;
    }

    public Integer getDiagnoseNum() {
        return diagnoseNum;
    }

    public void setDiagnoseNum(Integer diagnoseNum) {
        this.diagnoseNum = diagnoseNum;
    }

    public Integer getDiagnoseNumSubscribed() {
        return diagnoseNumSubscribed;
    }

    public void setDiagnoseNumSubscribed(Integer diagnoseNumSubscribed) {
        this.diagnoseNumSubscribed = diagnoseNumSubscribed;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(int segmentType) {
        this.segmentType = segmentType;
    }
}