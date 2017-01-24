package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/23.
 * 医生扫描获得的病人bean
 */

public class PaintBean {
    public String paintPhoto;
    public String paintName;
    public String paintAge;
    public String paintSex;

    public String getPaintPhoto() {
        return paintPhoto;
    }

    public void setPaintPhoto(String paintPhoto) {
        this.paintPhoto = paintPhoto;
    }

    public String getPaintName() {
        return paintName;
    }

    public void setPaintName(String paintName) {
        this.paintName = paintName;
    }

    public String getPaintAge() {
        return paintAge;
    }

    public void setPaintAge(String paintAge) {
        this.paintAge = paintAge;
    }

    public String getPaintSex() {
        return paintSex;
    }

    public void setPaintSex(String paintSex) {
        this.paintSex = paintSex;
    }

    @Override
    public String toString() {
        return "PaintBean{" +
                "paintPhoto='" + paintPhoto + '\'' +
                ", paintName='" + paintName + '\'' +
                ", paintAge='" + paintAge + '\'' +
                ", paintSex='" + paintSex + '\'' +
                '}';
    }
}
