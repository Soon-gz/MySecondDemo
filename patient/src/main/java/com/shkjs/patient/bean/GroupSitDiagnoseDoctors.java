package com.shkjs.patient.bean;

import java.io.Serializable;

public class GroupSitDiagnoseDoctors implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5643552008894461415L;

    private Long doctorId;
    private String doctorName;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

}
