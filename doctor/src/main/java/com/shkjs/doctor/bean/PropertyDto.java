package com.shkjs.doctor.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2017/1/9.
 * <p>
 * 配置信息
 */

public class PropertyDto implements Serializable {

    private boolean openPushMessage;//是否推送消息（开关）
    private boolean openAppMessage;//APP推送消息
    private boolean openShortMessage;//短消息
    private boolean openVoiceMessage;//语音通知
    private boolean orderPromotionDoctor;//推广医生
    private boolean orderFreeDoctor;//免费医生

    public boolean isOpenPushMessage() {
        return openPushMessage;
    }

    public void setOpenPushMessage(boolean openPushMessage) {
        this.openPushMessage = openPushMessage;
    }

    public boolean isOpenAppMessage() {
        return openAppMessage;
    }

    public void setOpenAppMessage(boolean openAppMessage) {
        this.openAppMessage = openAppMessage;
    }

    public boolean isOpenShortMessage() {
        return openShortMessage;
    }

    public void setOpenShortMessage(boolean openShortMessage) {
        this.openShortMessage = openShortMessage;
    }

    public boolean isOpenVoiceMessage() {
        return openVoiceMessage;
    }

    public void setOpenVoiceMessage(boolean openVoiceMessage) {
        this.openVoiceMessage = openVoiceMessage;
    }

    public boolean isOrderPromotionDoctor() {
        return orderPromotionDoctor;
    }

    public void setOrderPromotionDoctor(boolean orderPromotionDoctor) {
        this.orderPromotionDoctor = orderPromotionDoctor;
    }

    public boolean isOrderFreeDoctor() {
        return orderFreeDoctor;
    }

    public void setOrderFreeDoctor(boolean orderFreeDoctor) {
        this.orderFreeDoctor = orderFreeDoctor;
    }

    @Override
    public String toString() {
        return "PropertyDto{" +
                "openPushMessage=" + openPushMessage +
                ", openAppMessage=" + openAppMessage +
                ", openShortMessage=" + openShortMessage +
                ", openVoiceMessage=" + openVoiceMessage +
                ", orderPromotionDoctor=" + orderPromotionDoctor +
                ", orderFreeDoctor=" + orderFreeDoctor +
                '}';
    }
}
