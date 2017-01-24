package com.shkjs.patient.data.em;

/**
 * 用户金额变动类型
 *
 * @author ZHANGYUKUN
 */
public enum FlowType {
    RECHARGE("充值"),
    DRAWMONEY("提现"),
    EXPENSE("消费"),
    INCOME("收入"),
    EXPENSEREFUND("消费退款"),
    INCOMEREFUND("收费退款");

    private String mark;

    FlowType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
