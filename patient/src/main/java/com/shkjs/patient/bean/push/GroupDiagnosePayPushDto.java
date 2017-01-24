package com.shkjs.patient.bean.push;

import com.shkjs.patient.bean.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知用户支付
 *
 * @author LENOVO
 */

/**
 * 会诊支付
 *
 * @author LENOVO
 */
public class GroupDiagnosePayPushDto extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 5680014560334818701L;
    private String createName;
    private Long orderId;
    private Long diagnoseTime;
    private List<Long> doctorIds;
    private List<String> doctorNames;

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public List<String> getDoctorNames() {
        return doctorNames;
    }

    public void setDoctorNames(List<String> doctorNames) {
        this.doctorNames = doctorNames;
    }

    public void setDoctorName(String doctorName) {
        if (null == this.doctorNames) {
            this.doctorNames = new ArrayList<>();
        }

        this.doctorNames.add(doctorName);
    }


    public List<Long> getDoctorIds() {
        return doctorIds;
    }

    public void setDoctorIds(List<Long> doctorIds) {
        this.doctorIds = doctorIds;
    }

    public Long getDiagnoseTime() {
        return diagnoseTime;
    }

    public void setDiagnoseTime(Long diagnoseTime) {
        this.diagnoseTime = diagnoseTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
