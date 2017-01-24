package com.shkjs.patient.bean;

import com.shkjs.patient.data.em.DoctorLevel;
import com.shkjs.patient.data.em.DoctorPlatformLevel;
import com.shkjs.patient.data.em.DoctorTag;

public class Doctor extends UserInfo {
    /**
     *
     */
    private static final long serialVersionUID = -2354682518943517180L;

    private String workPermit;

    private String introduce;

    private String introduceVideo;

    private String otherPermit;

    private String skilled;

    private String doctorPermit;

    private String identityPermitFront;

    private String identityPermitReverse;

    private String hospitalName;

    private String categoryName;

    private Long hospitalId;

    private Long medicalCategoryId;

    private Long viewHospitalFee;

    private Long askHospitalFee;

    private DoctorLevel level;

    private DoctorPlatformLevel platformLevel;

    private Hospital hospital;

    private MedicalCategory medicalCategory;

    private DoctorTag tag;

    public String getWorkPermit() {
        return workPermit;
    }

    public void setWorkPermit(String workPermit) {
        this.workPermit = workPermit;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIntroduceVideo() {
        return introduceVideo;
    }

    public void setIntroduceVideo(String introduceVideo) {
        this.introduceVideo = introduceVideo;
    }

    public String getOtherPermit() {
        return otherPermit;
    }

    public void setOtherPermit(String otherPermit) {
        this.otherPermit = otherPermit;
    }

    public String getSkilled() {
        return skilled;
    }

    public void setSkilled(String skilled) {
        this.skilled = skilled;
    }

    public String getDoctorPermit() {
        return doctorPermit;
    }

    public void setDoctorPermit(String doctorPermit) {
        this.doctorPermit = doctorPermit;
    }

    public String getIdentityPermitFront() {
        return identityPermitFront;
    }

    public void setIdentityPermitFront(String identityPermitFront) {
        this.identityPermitFront = identityPermitFront;
    }

    public String getIdentityPermitReverse() {
        return identityPermitReverse;
    }

    public void setIdentityPermitReverse(String identityPermitReverse) {
        this.identityPermitReverse = identityPermitReverse;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Long getMedicalCategoryId() {
        return medicalCategoryId;
    }

    public void setMedicalCategoryId(Long medicalCategoryId) {
        this.medicalCategoryId = medicalCategoryId;
    }

    public Long getViewHospitalFee() {
        return viewHospitalFee;
    }

    public void setViewHospitalFee(Long viewHospitalFee) {
        this.viewHospitalFee = viewHospitalFee;
    }

    public Long getAskHospitalFee() {
        return askHospitalFee == null ? 0l : askHospitalFee;
    }

    public void setAskHospitalFee(Long askHospitalFee) {
        this.askHospitalFee = askHospitalFee;
    }

    public DoctorLevel getLevel() {
        return level == null ? DoctorLevel.RESIDENTDOCTOR : level;
    }

    public void setLevel(DoctorLevel level) {
        this.level = level;
    }

    public DoctorPlatformLevel getPlatformLevel() {
        return platformLevel == null ? DoctorPlatformLevel.CERTIFICATION : platformLevel;
    }

    public void setPlatformLevel(DoctorPlatformLevel platformLevel) {
        this.platformLevel = platformLevel;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public MedicalCategory getMedicalCategory() {
        return medicalCategory;
    }

    public void setMedicalCategory(MedicalCategory medicalCategory) {
        this.medicalCategory = medicalCategory;
    }

    public DoctorTag getTag() {
        return tag == null ? DoctorTag.NORMAL : tag;
    }

    public void setTag(DoctorTag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "workPermit='" + workPermit + '\'' +
                ", introduce='" + introduce + '\'' +
                ", introduceVideo='" + introduceVideo + '\'' +
                ", otherPermit='" + otherPermit + '\'' +
                ", skilled='" + skilled + '\'' +
                ", doctorPermit='" + doctorPermit + '\'' +
                ", identityPermitFront='" + identityPermitFront + '\'' +
                ", identityPermitReverse='" + identityPermitReverse + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", hospitalId=" + hospitalId +
                ", medicalCategoryId=" + medicalCategoryId +
                ", viewHospitalFee=" + viewHospitalFee +
                ", askHospitalFee=" + askHospitalFee +
                ", level=" + level +
                ", platformLevel=" + platformLevel +
                ", hospital=" + hospital +
                ", medicalCategory=" + medicalCategory +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((Doctor) obj).getId().equals(this.getId())) {
            return true;
        }
        return super.equals(obj);
    }
}