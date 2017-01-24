package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;
import com.shkjs.patient.data.em.CancelType;

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
    private int orderSource;
    private int cancelType = CancelType.DOCTOR.ordinal();
    private Long orderId;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getCancelType() {
        return cancelType;
    }

    public void setCancelType(int cancelType) {
        this.cancelType = cancelType;
    }
}
