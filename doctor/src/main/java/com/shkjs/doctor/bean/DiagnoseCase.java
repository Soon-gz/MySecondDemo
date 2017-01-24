package com.shkjs.doctor.bean;


import com.shkjs.doctor.base.BaseModel;

public class DiagnoseCase extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = 4299776375735256471L;

    private Long id;

    private Long healthReportsId;

    private Long doctorId;

    private String content;

    private String attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getHealthReportsId() {
        return healthReportsId;
    }

    public void setHealthReportsId(Long healthReportsId) {
        this.healthReportsId = healthReportsId;
    }

    public String getContent() {
        return content;
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
}