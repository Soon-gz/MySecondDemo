package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;


/**
 * 订单过期
 *
 * @author LENOVO
 */
public class OrderExpirePushDto extends Message {
    /**
     *
     */
    private static final long serialVersionUID = -4193817979221688940L;
    @Deprecated
    private String name;
    private String subscribeTime;
    private int orderSource;
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
