package com.shkjs.patient.data.em;

/**
 * 扣款类型
 *
 * @author ZHANGYUKUN
 */
public enum CutDownType {
    CT("认证图文"), AT("权威图文"), CV("认证视频"), AV("权威视频");

    private String mark;

    CutDownType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
