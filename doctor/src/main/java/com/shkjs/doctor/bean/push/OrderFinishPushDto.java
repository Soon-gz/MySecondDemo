package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.bean.Message;

/**
 * Created by Administrator on 2016/12/7.
 */

public class OrderFinishPushDto extends Message {
    private String userName;
    private int orderSource;
    private Long money;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(int orderSource) {
        this.orderSource = orderSource;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
