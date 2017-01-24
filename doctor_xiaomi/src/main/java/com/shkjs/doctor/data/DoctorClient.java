package com.shkjs.doctor.data;

import com.shkjs.doctor.bean.Version;

import java.io.Serializable;

/**
 * Created by xiaohu on 2017/1/6.
 * <p>
 * 版本信息
 */

public class DoctorClient implements Serializable {

    private Version androidUserClientVersion;
    private Version androidDoctorClientVersion;
    private Version iosUserClientVersion;
    private Version iosDoctorClientVersion;

    public Version getAndroidUserClientVersion() {
        return androidUserClientVersion;
    }

    public void setAndroidUserClientVersion(Version androidUserClientVersion) {
        this.androidUserClientVersion = androidUserClientVersion;
    }

    public Version getAndroidDoctorClientVersion() {
        return androidDoctorClientVersion;
    }

    public void setAndroidDoctorClientVersion(Version androidDoctorClientVersion) {
        this.androidDoctorClientVersion = androidDoctorClientVersion;
    }

    public Version getIosUserClientVersion() {
        return iosUserClientVersion;
    }

    public void setIosUserClientVersion(Version iosUserClientVersion) {
        this.iosUserClientVersion = iosUserClientVersion;
    }

    public Version getIosDoctorClientVersion() {
        return iosDoctorClientVersion;
    }

    public void setIosDoctorClientVersion(Version iosDoctorClientVersion) {
        this.iosDoctorClientVersion = iosDoctorClientVersion;
    }

    @Override
    public String toString() {
        return "DoctorClient{" +
                "androidUserClientVersion=" + androidUserClientVersion +
                ", androidDoctorClientVersion=" + androidDoctorClientVersion +
                ", iosUserClientVersion=" + iosUserClientVersion +
                ", iosDoctorClientVersion=" + iosDoctorClientVersion +
                '}';
    }
}
