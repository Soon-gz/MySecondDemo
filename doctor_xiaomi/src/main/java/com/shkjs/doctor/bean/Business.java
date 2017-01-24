package com.shkjs.doctor.bean;


import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.CancelType;
import com.shkjs.doctor.data.GroupSitDiagnose;
import com.shkjs.doctor.data.OrderStatus;

import java.io.Serializable;

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
    private UserInfoBean user;
    private DoctorBean doctor;
    private Order order;
    private OrderStatus status;
    private CancelType cancelType;
    private SitDiagnose sitDiagnose;
    private GroupSitDiagnose groupSitDiagnose;
    private Long confirmDate;
    private Long agreeDate;
    private Long startDate;


    public GroupSitDiagnose getGroupSitDiagnose() {
        return groupSitDiagnose;
    }

    public void setGroupSitDiagnose(GroupSitDiagnose groupSitDiagnose) {
        this.groupSitDiagnose = groupSitDiagnose;
    }

    public long getId() {
        return id;
    }

    public Long getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Long confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Long getAgreeDate() {
        return agreeDate;
    }

    public void setAgreeDate(Long agreeDate) {
        this.agreeDate = agreeDate;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
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

    public UserInfoBean getUser() {
        return user;
    }

    public void setUser(UserInfoBean user) {
        this.user = user;
    }

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
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
                '}';
    }
}
