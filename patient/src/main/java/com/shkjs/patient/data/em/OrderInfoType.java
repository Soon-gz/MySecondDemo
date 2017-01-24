package com.shkjs.patient.data.em;

/**
 * Created by xiaohu on 2016/10/20.
 * <p>
 * 预约类型说明
 */

public enum OrderInfoType {

    PICTURE("图文咨询"), VIDEO("视频预约"), MULTIVIDEO("多人会诊");

    private String mark;

    OrderInfoType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
