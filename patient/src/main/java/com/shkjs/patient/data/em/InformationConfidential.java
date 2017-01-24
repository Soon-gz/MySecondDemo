package com.shkjs.patient.data.em;

/**
 * 用户信息可见性描述
 *
 * @author ZHANGYUKUN
 */
public enum InformationConfidential {
    OPEN("所有医生可见"), SPECIFICOPEN("就诊医生可见"), CLOSE("仅自己可见");

    private String mark;

    InformationConfidential(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
