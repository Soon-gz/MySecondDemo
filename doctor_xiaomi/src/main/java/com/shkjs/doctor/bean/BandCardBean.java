package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/10/14.
 */

public class BandCardBean {

    /**
     * bankCard : string
     * createDate : 2016-12-06T05:40:36.892Z
     * defaultUse : USE
     * id : 0
     * userId : 0
     */

    private String bankCard;
    private String createDate;
    private String defaultUse;
    private int id;
    private int userId;

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDefaultUse() {
        return defaultUse;
    }

    public void setDefaultUse(String defaultUse) {
        this.defaultUse = defaultUse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
