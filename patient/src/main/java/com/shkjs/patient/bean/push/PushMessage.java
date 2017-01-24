package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Message;
import com.shkjs.patient.data.em.PushMessageTypeEm;
import com.shkjs.patient.data.em.RolesEm;

import java.util.List;

public class PushMessage extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 6650299974433708951L;


    private long userId;

    private long orderId;

    private long diagnoseTime;

    private List<Long> doctorIds;

    private List<Doctor> doctors;

    private String status;

    private RolesEm roles;

    private PushMessageTypeEm action;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(long diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    public List<Long> getDoctorIds() {
        return doctorIds;
    }

    public void setDoctorIds(List<Long> doctorIds) {
        this.doctorIds = doctorIds;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RolesEm getRoles() {
        return roles;
    }

    public void setRoles(RolesEm roles) {
        this.roles = roles;
    }

    public PushMessageTypeEm getAction() {
        return action;
    }

    public void setAction(PushMessageTypeEm action) {
        this.action = action;
    }
}