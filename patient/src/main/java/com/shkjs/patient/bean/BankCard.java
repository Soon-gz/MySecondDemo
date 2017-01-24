package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class BankCard extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = 8193858774575450320L;

    private Long id;

    private Long userId;

    private String bankCard;

    private String defaultUser;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard == null ? null : bankCard.trim();
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser == null ? null : defaultUser.trim();
    }

}