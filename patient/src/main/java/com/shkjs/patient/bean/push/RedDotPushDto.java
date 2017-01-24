package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;
import com.shkjs.patient.data.em.RedDotType;

import java.io.Serializable;

public class RedDotPushDto extends Message implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7999188688078218874L;

    private int redDotType;

    public int getRedDotType() {
        return redDotType;
    }

    public void setRedDotType(RedDotType redDotType) {
        this.redDotType = redDotType.ordinal();
    }
}