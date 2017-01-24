package com.shkjs.doctor.bean;

import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/9/21.
 * <p>
 * 咨询消息
 */
public class ConsultMessage implements Serializable {

    private PictureListBean messInfo;
    private RecentContact contact;

    public PictureListBean getMessInfo() {
        return messInfo;
    }

    public void setMessInfo(PictureListBean messInfo) {
        this.messInfo = messInfo;
    }

    public RecentContact getContact() {
        return contact;
    }

    public void setContact(RecentContact contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ConsultMessage{" +
                "messInfo=" + messInfo +
                ", contact=" + contact +
                '}';
    }
}
