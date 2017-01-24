package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class MedicalCategory extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = 2503958115188822570L;

    private Long id;

    private String name;

    private String deptImg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDeptImg() {
        return deptImg;
    }

    public void setDeptImg(String deptImg) {
        this.deptImg = deptImg;
    }

}