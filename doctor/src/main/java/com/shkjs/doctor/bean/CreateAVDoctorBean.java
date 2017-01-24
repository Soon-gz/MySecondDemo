package com.shkjs.doctor.bean;

/**
 * Created by Administrator on 2016/10/25.
 */

public class CreateAVDoctorBean {
    private String name;
    private String department;
    private String hospital;
    private String doctorTitle;
    private String head;

    public CreateAVDoctorBean() {
    }

    public CreateAVDoctorBean(String name, String department, String hospital, String doctorTitle, String head) {
        this.name = name;
        this.department = department;
        this.hospital = hospital;
        this.doctorTitle = doctorTitle;
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDoctorTitle() {
        return doctorTitle;
    }

    public void setDoctorTitle(String doctorTitle) {
        this.doctorTitle = doctorTitle;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
