package com.shkjs.patient.base;

import java.io.Serializable;

public class BaseModel implements Serializable {

    private static final long serialVersionUID = -5148252756064029013L;


    private String createDate;

    private String updateDate;

    private String dr;

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

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

}
