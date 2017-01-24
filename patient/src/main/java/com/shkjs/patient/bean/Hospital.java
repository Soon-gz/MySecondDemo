package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class Hospital extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = -905578332076187786L;

    private Long id;

    private String hospitalName;

    private String logo;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
