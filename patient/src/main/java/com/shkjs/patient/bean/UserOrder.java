package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/10/31.
 * <p>
 * 用户订单信息，包含业务逻辑
 */

public class UserOrder implements Serializable {

    private Order order;
    private Business business;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "order=" + order +
                ", business=" + business +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (null == ((UserOrder) obj).getOrder()) {
            return false;
        }
        if (null == this.getOrder()) {
            return false;
        }
        if (((UserOrder) obj).getOrder().getId().equals(this.getOrder().getId())) {
            return true;
        }
        return super.equals(obj);
    }
}
