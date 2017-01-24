package com.shkjs.doctor.bean;


import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.OrderSource;
import com.shkjs.doctor.data.OrderStatus;
import com.shkjs.doctor.data.OrderType;
import com.shkjs.doctor.data.PayType;

import java.util.Date;

public class Order extends BaseModel {

    private static final long serialVersionUID = 5875581284843956892L;

    private Long id;

    private Long money;

    private Long actuallyPaidMoney;

    private Long payerId;

    private Long receiverId;

    private OrderType type;

    private PayType payType;

    private String payOrigin;

    private OrderStatus status;

    private String code;

    private String mark;

    private Long pId;

    private Date payDate;

    private OrderSource source;

    public OrderSource getSource() {
        return source;
    }

    public void setSource(OrderSource source) {
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }


    public String getPayOrigin() {
        return payOrigin;
    }

    public void setPayOrigin(String payOrigin) {
        this.payOrigin = payOrigin == null ? null : payOrigin.trim();
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Long getActuallyPaidMoney() {
        return actuallyPaidMoney;
    }

    public void setActuallyPaidMoney(Long actuallyPaidMoney) {
        this.actuallyPaidMoney = actuallyPaidMoney;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", money=" + money +
                ", actuallyPaidMoney=" + actuallyPaidMoney +
                ", payerId=" + payerId +
                ", receiverId=" + receiverId +
                ", type=" + type +
                ", payType=" + payType +
                ", payOrigin='" + payOrigin + '\'' +
                ", status=" + status +
                ", code='" + code + '\'' +
                ", mark='" + mark + '\'' +
                ", pId=" + pId +
                ", payDate=" + payDate +
                ", source=" + source +
                '}';
    }
}