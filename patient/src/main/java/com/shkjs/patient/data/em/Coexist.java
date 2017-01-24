package com.shkjs.patient.data.em;

/**
 * 共存状态
 *
 * @author ZHANGYUKUN
 */
public enum Coexist {
    YES("共存"), NO("不共存");

    private String mark;

    Coexist(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
