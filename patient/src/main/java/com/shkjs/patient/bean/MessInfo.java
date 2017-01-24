package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.OrderType;
import com.shkjs.patient.data.em.PayType;

import java.io.Serializable;

public class MessInfo extends BaseModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7968785905534970729L;

    private Long id;

    private Long money;

    private Long actuallyPaidMoney;

    private UserInfo userInfo;

    private Doctor doctor;

    private OrderType type;

    private PayType payType;

    private String payOrigin;

    private OrderStatus status;

    private String code;

    private String mark;

    private Long pId;

    private String payDate;

    private OrderSource source;

    private String diagnoseDate;

    private HealthReport healthReport;

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

    public Long getActuallyPaidMoney() {
        return actuallyPaidMoney;
    }

    public void setActuallyPaidMoney(Long actuallyPaidMoney) {
        this.actuallyPaidMoney = actuallyPaidMoney;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public String getPayOrigin() {
        return payOrigin;
    }

    public void setPayOrigin(String payOrigin) {
        this.payOrigin = payOrigin;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public OrderSource getSource() {
        return source;
    }

    public void setSource(OrderSource source) {
        this.source = source;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getDiagnoseDate() {
        return diagnoseDate;
    }

    public void setDiagnoseDate(String diagnoseDate) {
        this.diagnoseDate = diagnoseDate;
    }

    public HealthReport getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(HealthReport healthReport) {
        this.healthReport = healthReport;
    }


    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (null == ((MessInfo) obj).doctor) {
            return false;
        }
        if (null == ((MessInfo) obj).userInfo) {
            return false;
        }
        if (null == ((MessInfo) obj).source) {
            return false;
        }
        if (((MessInfo) obj).source.equals(OrderSource.INQUIRY_RESERVE) && this.source.equals(OrderSource
                .INQUIRY_RESERVE)) {
            if (((MessInfo) obj).doctor.equals(this.doctor) && ((MessInfo) obj).userInfo.equals(this.userInfo)) {
                return true;
            }
        }
        if (((MessInfo) obj).id.equals(this.id)) {
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "MessInfo{" +
                "id=" + id +
                ", money=" + money +
                ", actuallyPaidMoney=" + actuallyPaidMoney +
                ", userInfo=" + userInfo +
                ", doctor=" + doctor +
                ", type=" + type +
                ", payType=" + payType +
                ", payOrigin='" + payOrigin + '\'' +
                ", status=" + status +
                ", code='" + code + '\'' +
                ", mark='" + mark + '\'' +
                ", pId=" + pId +
                ", payDate='" + payDate + '\'' +
                ", source=" + source +
                ", diagnoseDate='" + diagnoseDate + '\'' +
                ", healthReport=" + healthReport +
                '}';
    }
}
