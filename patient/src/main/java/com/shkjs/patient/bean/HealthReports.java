package com.shkjs.patient.bean;

import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.ReportType;
import com.shkjs.patient.data.em.Sex;

public class HealthReports extends BaseModel {
    private static final long serialVersionUID = -7740050792787561731L;

    private Long id;

    private int patientAge;

    private Sex patientSex;

    private String patientName;

    private String simpleIntroduction;

    private Long seeDoctorTemplateId;

    private ReportType type;

    private Long userId;


    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientName() {
        return TextUtils.isEmpty(patientName) ? "未填写" : patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSimpleIntroduction() {
        return simpleIntroduction;
    }

    public void setSimpleIntroduction(String simpleIntroduction) {
        this.simpleIntroduction = simpleIntroduction == null ? null : simpleIntroduction.trim();
    }

    public Long getSeeDoctorTemplateId() {
        return seeDoctorTemplateId;
    }

    public void setSeeDoctorTemplateId(Long seeDoctorTemplateId) {
        this.seeDoctorTemplateId = seeDoctorTemplateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Sex getPatientSex() {
        return patientSex == null ? Sex.SECRECY : patientSex;
    }

    public void setPatientSex(Sex patientSex) {
        this.patientSex = patientSex;
    }


    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((HealthReports) obj).getId().equals(this.getId())) {
            return true;
        }
        return super.equals(obj);
    }
}