package com.shkjs.doctor.bean;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MedicalCategoryBean {
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
