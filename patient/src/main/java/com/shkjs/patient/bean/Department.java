package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/9/20.
 * <p/>
 * 科室
 */
public class Department implements Serializable {

    private long id;
    private String dr;
    private String name;
    private String iconUrl;
    private String createDate;
    private String updateDate;
    private String deptImg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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

    public String getDeptImg() {
        return deptImg;
    }

    public void setDeptImg(String deptImg) {
        this.deptImg = deptImg;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", dr='" + dr + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", deptImg='" + deptImg + '\'' +
                '}';
    }
}
