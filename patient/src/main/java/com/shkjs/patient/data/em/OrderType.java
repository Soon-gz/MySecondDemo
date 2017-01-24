package com.shkjs.patient.data.em;

/**
 * 消费类型
 *
 * @author ZHANGYUKUN
 */
public enum OrderType {
    RECHARGE("充值"), DRAWMONEY("提现"), EXPENSE("消费"), INCOME("收入");


    private String mark;

    OrderType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
