package com.shkjs.doctor.bean.push;


import com.shkjs.doctor.bean.Message;

/**
 * 订单取消
 *
 * @author LENOVO
 */

public class OrderCancelPushDto extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 7701388394162469701L;
    private String name;
    private String dateTime;
    private String subscribeTime;
    private int cancelType;
    private int orderSource;

    public int getCancelType() {
        return cancelType;
    }

    public void setCancelType(int cancelType) {
        this.cancelType = cancelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public int getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(int orderSource) {
        this.orderSource = orderSource;
    }
}
