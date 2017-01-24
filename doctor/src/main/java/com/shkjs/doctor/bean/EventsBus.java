package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/28.
 */

public class EventsBus {
    private String message;
    private String type;

    public EventsBus(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}
