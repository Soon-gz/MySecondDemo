package com.shkjs.patient.bean;

import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/9/21.
 * <p>
 * 咨询消息
 */
public class ConsultMessage implements Serializable {

    private MessInfo messInfo;
    private RecentContact contact;
    private boolean isDelete;

    public MessInfo getMessInfo() {
        return messInfo;
    }

    public void setMessInfo(MessInfo messInfo) {
        this.messInfo = messInfo;
    }

    public RecentContact getContact() {
        return contact;
    }

    public void setContact(RecentContact contact) {
        this.contact = contact;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (null == ((ConsultMessage) obj).getMessInfo()) {
            return false;
        } else if (null == this.getMessInfo()) {
            return false;
        } else {
            if (((ConsultMessage) obj).getMessInfo().equals(this.getMessInfo())) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ConsultMessage{" +
                "messInfo=" + messInfo +
                ", contact=" + contact +
                ", isDelete=" + isDelete +
                '}';
    }
}
