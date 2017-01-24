package com.shkjs.patient.bean;

/**
 * 密保问题
 */
public class SecurityQuestion {
    private Long id;

    private String dr;

    private String createDate;

    private String updateDate;

    private String question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr == null ? null : dr.trim();
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((SecurityQuestion) obj).id.equals(this.id)) {
            return true;
        }
        return super.equals(obj);
    }
}