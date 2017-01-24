package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.InquiryStatus;

public class InquiryReserve extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -6330477212421348688L;

    private Long id;

    private User user;

    private User doctor;

    private InquiryStatus status;

    private Order order;

    private Long healthReportId;

    private Long money;

    private String cancelUser;

    public String getCancelUser() {
        return cancelUser;
    }

    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }

    public Long getCancelUserId() {
        return cancelUserId;
    }

    public void setCancelUserId(Long cancelUserId) {
        this.cancelUserId = cancelUserId;
    }

    private Long cancelUserId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public static InquiryReserve newInquiryReserve() {
        InquiryReserve inquiryReserve = new InquiryReserve();
        inquiryReserve.setStatus(InquiryStatus.INITIAL);
        return inquiryReserve;
    }

    public Long getHealthReportId() {
        return healthReportId;
    }

    public void setHealthReportId(Long healthReportId) {
        this.healthReportId = healthReportId;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}