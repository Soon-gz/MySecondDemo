package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.FlowType;

import java.util.Map;

public class AccountFlowDetail extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -6194807527464330019L;

    private Long id;

    private Account account;

    private Long money;

    private FlowType type;

    private String mark;

    private Long orderId;


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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark == null ? null : mark.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long queryIncome(Map<String, Object> map) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((AccountFlowDetail) obj).id.equals(this.id)) {
            return true;
        }
        return super.equals(obj);
    }
}