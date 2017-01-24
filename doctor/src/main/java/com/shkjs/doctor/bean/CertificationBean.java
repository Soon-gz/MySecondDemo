package com.shkjs.doctor.bean;

import java.io.Serializable;

/**
 * Created by Shuwen on 2016/10/9.
 */

public class CertificationBean implements Serializable{
    private String certificateName;
    private String certificateHospital;
    private String certificateSubject;
    private String certificateZhicheng;
    private String certificateShanchang;
    private String certificateJianjie;

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getCertificateHospital() {
        return certificateHospital;
    }

    public void setCertificateHospital(String certificateHospital) {
        this.certificateHospital = certificateHospital;
    }

    public String getCertificateSubject() {
        return certificateSubject;
    }

    public void setCertificateSubject(String certificateSubject) {
        this.certificateSubject = certificateSubject;
    }

    public String getCertificateZhicheng() {
        return certificateZhicheng;
    }

    public void setCertificateZhicheng(String certificateZhicheng) {
        this.certificateZhicheng = certificateZhicheng;
    }

    public String getCertificateShanchang() {
        return certificateShanchang;
    }

    public void setCertificateShanchang(String certificateShanchang) {
        this.certificateShanchang = certificateShanchang;
    }

    public String getCertificateJianjie() {
        return certificateJianjie;
    }

    public void setCertificateJianjie(String certificateJianjie) {
        this.certificateJianjie = certificateJianjie;
    }

    @Override
    public String toString() {
        return "CertificationBean{" +
                "certificateName='" + certificateName + '\'' +
                ", certificateHospital='" + certificateHospital + '\'' +
                ", certificateSubject='" + certificateSubject + '\'' +
                ", certificateZhicheng='" + certificateZhicheng + '\'' +
                ", certificateShanchang='" + certificateShanchang + '\'' +
                ", certificateJianjie='" + certificateJianjie + '\'' +
                '}';
    }
}
