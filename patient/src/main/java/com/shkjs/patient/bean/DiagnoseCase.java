package com.shkjs.patient.bean;

import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.Sex;

public class DiagnoseCase extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = 4299776375735256471L;

    private Long id;

    private Long healthReportId;

    private Long doctorId;

    private String content;

    private Sex sex;

    private int age;

    private String name;

    private String exhort;

    private String attachment;

    private UserInfo doctorInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getContent() {
        return TextUtils.isEmpty(content) ? "未填写" : content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getHealthReportId() {
        return healthReportId;
    }

    public void setHealthReportId(Long healthReportId) {
        this.healthReportId = healthReportId;
    }

    public String getExhort() {
        return TextUtils.isEmpty(exhort) ? "未填写" : exhort;
    }

    public void setExhort(String exhort) {
        this.exhort = exhort;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserInfo getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(UserInfo doctorInfo) {
        this.doctorInfo = doctorInfo;
    }
}