package com.shkjs.patient.data.em;

/**
 * 消息开关
 *
 * @author ZHANGYUKUN
 */
public enum NotificationSwitch {

    OPEN("打开"), CLOSE("关闭");

    private String mark;

    NotificationSwitch(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
