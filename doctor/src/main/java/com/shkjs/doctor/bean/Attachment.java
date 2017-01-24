package com.shkjs.doctor.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/11/2.
 */

public class Attachment implements Serializable {

    private AttachmentContent picture;
    private AttachmentContent voice;
    private AttachmentContent video;

    public AttachmentContent getPicture() {
        return picture;
    }

    public void setPicture(AttachmentContent picture) {
        this.picture = picture;
    }

    public AttachmentContent getVoice() {
        return voice;
    }

    public void setVoice(AttachmentContent voice) {
        this.voice = voice;
    }

    public AttachmentContent getVideo() {
        return video;
    }

    public void setVideo(AttachmentContent video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "picture=" + picture +
                ", voice=" + voice +
                ", video=" + video +
                '}';
    }
}
