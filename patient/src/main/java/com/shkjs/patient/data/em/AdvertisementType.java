package com.shkjs.patient.data.em;

/**
 * 活动类型
 *
 * @author ZHANGYUKUN
 */
public enum AdvertisementType {

    //活动指的是app视图页面
    LINK("链接"), ACTIVITY("活动"), SHOW("显示");

    private String mark;

    AdvertisementType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
