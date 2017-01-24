package com.shkjs.patient.data.em;

/**
 * 订单状态
 *
 * @author ZHANGYUKUN
 */
public enum OrderStatus {
    ALL("全部"), INITIAL("待支付"), EXPIRE("过期"), AGREE("同意"), PAID("未完成"), COMPLETE("完成"), CANCEL("可退款"), REFUND("已退款");

    private String mark;

    OrderStatus(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
