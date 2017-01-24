package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.OrderType;
import com.shkjs.patient.data.em.PayType;

public class Order extends BaseModel {

    private static final long serialVersionUID = 5875581284843956892L;

    private Long id;

    private Long money;

    private Long refundMoney;

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

    private String payDate;

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
        return money == null ? 0l : money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Long getRefundMoney() {
        return refundMoney == null ? 0l : refundMoney;
    }

    public void setRefundMoney(Long refundMoney) {
        this.refundMoney = refundMoney;
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
        return actuallyPaidMoney == null ? 0l : actuallyPaidMoney;
    }

    public void setActuallyPaidMoney(Long actuallyPaidMoney) {
        this.actuallyPaidMoney = actuallyPaidMoney;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
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