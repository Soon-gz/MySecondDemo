package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/27.
 */

public class MyPatintBean {
    private String myPatientName;
    private String myPatientAge;
    private String myPatientSex;
    private String myPatientPhoto;

    public String getMyPatientName() {
        return myPatientName;
    }

    public void setMyPatientName(String myPatientName) {
        this.myPatientName = myPatientName;
    }

    public String getMyPatientAge() {
        return myPatientAge;
    }

    public void setMyPatientAge(String myPatientAge) {
        this.myPatientAge = myPatientAge;
    }

    public String getMyPatientSex() {
        return myPatientSex;
    }

    public void setMyPatientSex(String myPatientSex) {
        this.myPatientSex = myPatientSex;
    }

    public String getMyPatientPhoto() {
        return myPatientPhoto;
    }

    public void setMyPatientPhoto(String myPatientPhoto) {
        this.myPatientPhoto = myPatientPhoto;
    }

    @Override
    public String toString() {
        return "MyPatintBean{" +
                "myPatientName='" + myPatientName + '\'' +
                ", myPatientAge='" + myPatientAge + '\'' +
                ", myPatientSex='" + myPatientSex + '\'' +
                ", myPatientPhoto='" + myPatientPhoto + '\'' +
                '}';
    }
}
