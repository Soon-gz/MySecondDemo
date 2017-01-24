package com.shkjs.patient.data.em;

/**
 * Created by xiaohu on 2017/1/5.
 * <p>
 * 医生标签
 */

public enum DoctorTag {

    NORMAL("默认"),
    PROMOTION("推广"),
    FREE("免费");

    private String mark;

    DoctorTag(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
