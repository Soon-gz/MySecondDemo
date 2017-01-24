package com.shkjs.doctor.data;

/**
 * 推送动作
 *
 * @author LENOVO
 */
public enum PushMessageTypeEm {
    RED_DOT("红点"),
    RAW_DATA("原始消息"),
    AUTHENTICATION("医生认证"),
    RECHARGE("充值"),
    PAY("支付"),
    FAMILY_PAY("家庭组支付"),
    FAMILY("家庭组"),
    ORDER_CANCEL("订单取消"),
    ORDER_OVERTIME("订单超时"),
    DIAGNOSE("视频坐诊"),
    GROUP_DIAGNOSE("会诊"),
    GROUP_DIAGNOSE_ROOMINFO("会诊房间信息"),
    ORDER_FINISH("订单完成"),
    MONTH_CLOSING("结算");


    private String mark;

    PushMessageTypeEm(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
