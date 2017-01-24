package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/29.
 */

public class DoctorMessageBean {
    private String doctorId;                //医生表的ID
    private String doctorName;              //医生姓名
    private String doctorSubject;            //医生的科室（骨科）
    private String doctorHeadPhoto;         //医生的头像
    private String doatorCertificate;       //医生的认证等级(已认证、专家)
    private String doctorIsCertifucated;    //是否已验证(0=已验证，1=正在验证，2=未验证，3=认证失败)
    private String isHasPatient;            //是否有患者预约

    public String getIsHasPatient() {
        return isHasPatient;
    }

    public void setIsHasPatient(String isHasPatient) {
        this.isHasPatient = isHasPatient;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSubject() {
        return doctorSubject;
    }

    public void setDoctorSubject(String doctorSubject) {
        this.doctorSubject = doctorSubject;
    }

    public String getDoctorHeadPhoto() {
        return doctorHeadPhoto;
    }

    public void setDoctorHeadPhoto(String doctorHeadPhoto) {
        this.doctorHeadPhoto = doctorHeadPhoto;
    }

    public String getDoatorCertificate() {
        return doatorCertificate;
    }

    public void setDoatorCertificate(String doatorCertificate) {
        this.doatorCertificate = doatorCertificate;
    }

    public String getDoctorIsCertifucated() {
        return doctorIsCertifucated;
    }

    public void setDoctorIsCertifucated(String doctorIsCertifucated) {
        this.doctorIsCertifucated = doctorIsCertifucated;
    }

    @Override
    public String toString() {
        return "DoctorMessageBean{" +
                "doctorId='" + doctorId + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", doctorSubject='" + doctorSubject + '\'' +
                ", doctorHeadPhoto='" + doctorHeadPhoto + '\'' +
                ", doatorCertificate='" + doatorCertificate + '\'' +
                ", doctorIsCertifucated='" + doctorIsCertifucated + '\'' +
                '}';
    }
}
