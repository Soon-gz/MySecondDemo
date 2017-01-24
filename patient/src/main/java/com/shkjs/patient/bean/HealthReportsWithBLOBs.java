package com.shkjs.patient.bean;

import com.raspberry.library.util.TextUtils;

public class HealthReportsWithBLOBs extends HealthReports {
    /**
     *
     */
    private static final long serialVersionUID = -4112101147136827021L;

    private String content;

    private Attachment attachmentModel;

    private String diseaseDescription;

    public String getContent() {
        return TextUtils.isEmpty(content) ? "未填写" : content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Attachment getAttachment() {
        return attachmentModel;
    }

    public void setAttachment(Attachment attachment) {
        this.attachmentModel = attachment == null ? null : attachment;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription == null ? null : diseaseDescription.trim();
    }

    @Override
    public String toString() {
        return "HealthReportsWithBLOBs{" +
                "content='" + content + '\'' +
                ", attachmentModel=" + attachmentModel +
                ", diseaseDescription='" + diseaseDescription + '\'' +
                '}';
    }
}