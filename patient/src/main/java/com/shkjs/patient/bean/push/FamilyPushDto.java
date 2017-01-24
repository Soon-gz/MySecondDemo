package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;

/**
 * 家庭组成员变更内容
 *
 * @author LENOVO
 */
public class FamilyPushDto extends Message {

    /**
     *
     */
    private static final long serialVersionUID = -8021452372959665918L;
    private int oper;
    private String name;
    private String dateTime;

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

    public int getOper() {
        return oper;
    }

    public void setOper(int oper) {
        this.oper = oper;
    }
}
