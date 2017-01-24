package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;

/**
 * 消费推送
 *
 * @author LENOVO
 */
public class ExpensePushDto extends Message {

    /**
     *
     */
    private static final long serialVersionUID = 699591297000427389L;

    private String dateTime;
    private int orderSource;
    private Long money;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
