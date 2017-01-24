package com.shkjs.nim.em;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/10/31.
 */

public enum ClientType implements Serializable {

    DOCTOR(0),//医生端
    PATIENT(1),//患者端
    DOCTORMAIN(2);//医生端主页跳转

    private int type;

    ClientType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
