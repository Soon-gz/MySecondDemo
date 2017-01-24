package com.shkjs.patient.bean;

/**
 * 可用性描述
 *
 * @author ZHANGYUKUN
 */
public enum UseStatus {
    USE("可用"), DISUSE("不可用");

    private String mark;

    UseStatus(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
