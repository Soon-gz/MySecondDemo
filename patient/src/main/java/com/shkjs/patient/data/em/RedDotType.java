package com.shkjs.patient.data.em;

public enum RedDotType {
    SYSTEM_MESSAGE("系统消息"),
    INQUIRY_RESERVE("图文咨询"),
    SIT_DIAGNOSE("坐诊"),
    GROUP_SIT_DIAGNOSE("会诊"),
    HEALTH_REPORT("健康报告");

    private String mark;

    RedDotType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
