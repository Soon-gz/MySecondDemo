package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/12/7.
 */

public class RefundOrder implements Serializable {

    private long userRefundMoney;
    private long actuallyPaidMoney;

    public long getUserRefundMoney() {
        return userRefundMoney;
    }

    public void setUserRefundMoney(long userRefundMoney) {
        this.userRefundMoney = userRefundMoney;
    }

    public long getActuallyPaidMoney() {
        return actuallyPaidMoney;
    }

    public void setActuallyPaidMoney(long actuallyPaidMoney) {
        this.actuallyPaidMoney = actuallyPaidMoney;
    }

    @Override
    public String toString() {
        return "RefundOrder{" +
                "userRefundMoney=" + userRefundMoney +
                ", actuallyPaidMoney=" + actuallyPaidMoney +
                '}';
    }
}
