package com.shkjs.patient.bean.push;

import com.google.gson.annotations.SerializedName;
import com.shkjs.patient.bean.Message;

/**
 * 家庭组消费的推送结构
 *
 * @author LENOVO
 */
public class ExpenseFamilyPushDto extends Message {

    /**
     *
     */
    private static final long serialVersionUID = -2668768582998751269L;

    private String name;
    private String dateTime;
    private int isUser;
    private int orderSource;
    private Long money;

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

    public int getIsUser() {
        return isUser;
    }

    public void setIsUser(int isUser) {
        this.isUser = isUser;
    }

    @Override
    public String toString() {
        return "ExpenseFamilyPushDto{" +
                "name='" + name + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", isUser=" + isUser +
                ", orderSource=" + orderSource +
                ", money=" + money +
                '}';
    }
}
