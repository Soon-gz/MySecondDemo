package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.OrderStatus;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaohu on 2016/11/1.
 * <p>
 * 业务逻辑相关数据
 */

public class Business extends BaseModel implements Serializable {

    private long id;
    private long money;
    private long orderId;
    private long userId;
    private long doctorId;
    private long sitDiagnoseId;
    private long healthReportId;
    private long cancelUserId;
    private UserInfo user;
    private Doctor doctor;
    private Order order;
    private OrderStatus status;
    private CancelType cancelType;
    //视频就诊
    private SitDiagnose sitDiagnose;
    //视频会诊
    private String confirmDate;
    private String startDate;
    private String roomName;
    private List<Doctor> doctorList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public long getSitDiagnoseId() {
        return sitDiagnoseId;
    }

    public void setSitDiagnoseId(long sitDiagnoseId) {
        this.sitDiagnoseId = sitDiagnoseId;
    }

    public long getHealthReportId() {
        return healthReportId;
    }

    public void setHealthReportId(long healthReportId) {
        this.healthReportId = healthReportId;
    }

    public long getCancelUserId() {
        return cancelUserId;
    }

    public void setCancelUserId(long cancelUserId) {
        this.cancelUserId = cancelUserId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public CancelType getCancelType() {
        return cancelType;
    }

    public void setCancelType(CancelType cancelType) {
        this.cancelType = cancelType;
    }

    public SitDiagnose getSitDiagnose() {
        return sitDiagnose;
    }

    public void setSitDiagnose(SitDiagnose sitDiagnose) {
        this.sitDiagnose = sitDiagnose;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", money=" + money +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", doctorId=" + doctorId +
                ", sitDiagnoseId=" + sitDiagnoseId +
                ", healthReportId=" + healthReportId +
                ", cancelUserId=" + cancelUserId +
                ", user=" + user +
                ", doctor=" + doctor +
                ", order=" + order +
                ", status=" + status +
                ", cancelType=" + cancelType +
                ", sitDiagnose=" + sitDiagnose +
                ", confirmDate='" + confirmDate + '\'' +
                ", startDate='" + startDate + '\'' +
                ", roomName='" + roomName + '\'' +
                ", doctorList=" + doctorList +
                '}';
    }
}
