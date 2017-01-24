package com.shkjs.patient.data.em;

/**
 * 订单来源描述
 *
 * @author ZHANGYUKUN
 */
public enum OrderSource {
    INQUIRY_RESERVE("问诊"), SIT_DIAGNOSE_RESERVE("坐诊"), GROUP_SIT_DIAGNOSE("会诊"), GROUP_SIT_DIAGNOSE_DETAIL("子会诊"),
    RECHARGE("未定义");

    private String mark;

    OrderSource(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
