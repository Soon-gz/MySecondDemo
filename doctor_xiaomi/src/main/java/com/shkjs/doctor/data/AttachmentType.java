package com.shkjs.doctor.data;

/**
 * Created by xiaohu on 2016/11/2.
 */

public enum AttachmentType {

    PICTURE("图片"), VOICE("语音"), VIDEO("视频");

    private String mark;

    AttachmentType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}


