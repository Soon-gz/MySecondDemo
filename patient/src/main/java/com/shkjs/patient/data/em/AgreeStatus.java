package com.shkjs.patient.data.em;

/**
 * 添加成员状态
 *
 * @author ZHANGYUKUN
 */
public enum AgreeStatus {
    AGREEMENT("同意"), DISAGREE("不同意"), ASK("询问中");

    private String mark;

    AgreeStatus(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
