package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.bean.Message;

/**
 * Created by Administrator on 2016/12/8.
 */

public class MonthClosingPushDto extends Message {
    private String dateTime;
    private String yearAndMonth;
    private Long money;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
