package com.shkjs.doctor.bean;

/**
 * Created by Administrator on 2016/11/28.
 */

public class GroupSitDiagnoseRoomInfo extends Message {
    private String roomName;
    private String roomId;
    private String orderCode;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderId) {
        this.orderCode = orderId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
