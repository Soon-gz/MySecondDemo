package com.shkjs.doctor.data;

/**
 * Created by Administrator on 2016/12/9.
 */

public class GroupSitDiagnose {

    /**
     * createDate : 1481280961000
     * id : 54
     * doctorId : 156
     * userId : 158
     * status : PAID
     * money : 60000
     * orderId : 630
     * cancelType : null
     * cancelUserId : null
     * healthReportId : 511
     * confirmDate : 1481284496000
     * agreeDate : 1481280983000
     * startDate : 1481288100000
     * roomName : 201612098666800000083
     * roomId : null
     * roomStatus : 0
     * groupSitDiagnoseDetail : null
     */

    private long createDate;
    private int id;
    private int doctorId;
    private int userId;
    private String status;
    private int money;
    private int orderId;
    private Object cancelType;
    private Object cancelUserId;
    private int healthReportId;
    private long confirmDate;
    private long agreeDate;
    private long startDate;
    private String roomName;
    private Object roomId;
    private String roomStatus;
    private Object groupSitDiagnoseDetail;

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Object getCancelType() {
        return cancelType;
    }

    public void setCancelType(Object cancelType) {
        this.cancelType = cancelType;
    }

    public Object getCancelUserId() {
        return cancelUserId;
    }

    public void setCancelUserId(Object cancelUserId) {
        this.cancelUserId = cancelUserId;
    }

    public int getHealthReportId() {
        return healthReportId;
    }

    public void setHealthReportId(int healthReportId) {
        this.healthReportId = healthReportId;
    }

    public long getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(long confirmDate) {
        this.confirmDate = confirmDate;
    }

    public long getAgreeDate() {
        return agreeDate;
    }

    public void setAgreeDate(long agreeDate) {
        this.agreeDate = agreeDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Object getRoomId() {
        return roomId;
    }

    public void setRoomId(Object roomId) {
        this.roomId = roomId;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Object getGroupSitDiagnoseDetail() {
        return groupSitDiagnoseDetail;
    }

    public void setGroupSitDiagnoseDetail(Object groupSitDiagnoseDetail) {
        this.groupSitDiagnoseDetail = groupSitDiagnoseDetail;
    }
}
