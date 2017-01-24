package com.shkjs.patient.bean;

import com.shkjs.patient.data.em.OrderInfoType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xiaohu on 2016/10/20.
 * <p>
 * 预约信息
 */

public class OrderInfo implements Serializable {

    private ArrayList<Doctor> doctors;
    private Order order;
    private OrderInfoType orderInfoType;
    private String time;
    private boolean isMuiltVideo;

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderInfoType getOrderInfoType() {
        return orderInfoType;
    }

    public void setOrderInfoType(OrderInfoType orderInfoType) {
        this.orderInfoType = orderInfoType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isMuiltVideo() {
        return isMuiltVideo;
    }

    public void setMuiltVideo(boolean muiltVideo) {
        isMuiltVideo = muiltVideo;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "doctors=" + doctors +
                ", order=" + order +
                ", orderInfoType=" + orderInfoType +
                ", time='" + time + '\'' +
                ", isMuiltVideo=" + isMuiltVideo +
                '}';
    }
}
