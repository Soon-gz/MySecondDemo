package com.shkjs.doctor.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/11/7.
 * <p>
 * 健康报告，语音、视频、图片文件内容
 */

public class BodyContent implements Serializable {

    private String address;
    private long duration;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "BodyContent{" +
                "address='" + address + '\'' +
                ", duration=" + duration +
                '}';
    }
}
