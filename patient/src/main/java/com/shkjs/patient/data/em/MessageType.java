package com.shkjs.patient.data.em;

/**
 * Created by xiaohu on 2016/10/26.
 * <p>
 * 消息类型
 */

public enum MessageType {

    PICTURE(0), VIDEO(1), VIDEO_SITTING(2), SYSTEM(3);

    private int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
