package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.bean.Message;

/**
 * Created by Administrator on 2016/11/23.
 */

public class RedDotPushDto extends Message{

    /**
     * type : 0
     * id : 0
     * data : 0
     */

    private int type;
    private int data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
