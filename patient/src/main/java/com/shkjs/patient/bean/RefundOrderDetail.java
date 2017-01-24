package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.PayType;

/**
 * Created by xiaohu on 2016/12/7.
 */

public class RefundOrderDetail extends BaseModel {

    private long id;
    private String code;
    private String mark;
    private long money;
    private String orderCode;
    private String payOrigin;
    private PayType payType;
    private OrderStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getPayOrigin() {
        return payOrigin;
    }

    public void setPayOrigin(String payOrigin) {
        this.payOrigin = payOrigin;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RefundOrderDetail{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", mark='" + mark + '\'' +
                ", money=" + money +
                ", orderCode='" + orderCode + '\'' +
                ", payOrigin='" + payOrigin + '\'' +
                ", payType=" + payType +
                ", status=" + status +
                '}';
    }
}
