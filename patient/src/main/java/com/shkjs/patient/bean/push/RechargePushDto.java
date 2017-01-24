package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;

/**
 * 充值
 *
 * @author LENOVO
 */
public class RechargePushDto extends Message {

    /**
     * 充值
     */
    private static final long serialVersionUID = -8333722881031611695L;

    private String name;     // 名字
    private String dateTime; // 日期时间
    private Long money;    // 金额


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

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
