package com.shkjs.patient.data.em;

/**
 * 折扣类型
 *
 * @author ZHANGYUKUN
 */
public enum DiscountType {
    FIX("固定金额"), PERCENT("百分比");

    private String mark;

    DiscountType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
