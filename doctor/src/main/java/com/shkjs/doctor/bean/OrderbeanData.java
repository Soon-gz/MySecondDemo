package com.shkjs.doctor.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/2.
 */

public class OrderbeanData {

    /**
     * actuallyPaidMoney : 0
     * code : string
     * createDate : 2016-11-02T06:15:05.583Z
     * dr : string
     * id : 0
     * mark : string
     * money : 0
     * pId : 0
     * payDate : 2016-11-02T06:15:05.583Z
     * payOrigin : string
     * payType : BALANCE
     * payerId : 0
     * receiverId : 0
     * source : INQUIRY_RESERVE
     * status : INITIAL
     * type : RECHARGE
     * updateDate : 2016-11-02T06:15:05.583Z
     */

    private Business business;
    private OrderBean order;

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public static class OrderBean implements Serializable {
        private int actuallyPaidMoney;
        private String code;
        private String createDate;
        private String dr;
        private int id;
        private String mark;
        private int money;
        private int pId;
        private String payDate;
        private String payOrigin;
        private String payType;
        private int payerId;
        private int receiverId;
        private String source;
        private String status;
        private String type;
        private String updateDate;

        public int getActuallyPaidMoney() {
            return actuallyPaidMoney;
        }

        public void setActuallyPaidMoney(int actuallyPaidMoney) {
            this.actuallyPaidMoney = actuallyPaidMoney;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getDr() {
            return dr;
        }

        public void setDr(String dr) {
            this.dr = dr;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getPId() {
            return pId;
        }

        public void setPId(int pId) {
            this.pId = pId;
        }

        public String getPayDate() {
            return payDate;
        }

        public void setPayDate(String payDate) {
            this.payDate = payDate;
        }

        public String getPayOrigin() {
            return payOrigin;
        }

        public void setPayOrigin(String payOrigin) {
            this.payOrigin = payOrigin;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public int getPayerId() {
            return payerId;
        }

        public void setPayerId(int payerId) {
            this.payerId = payerId;
        }

        public int getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(int receiverId) {
            this.receiverId = receiverId;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
    }
}
