package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class Account extends BaseModel {
    private static final long serialVersionUID = -6571963562718448141L;

    private Long id;

    private User user;

    private Long balance;

    private Long freeze;

    private Integer version;

    private Long totalIncome;

    private Long totalExpend;

    public Account() {
    }

    public Account(Long accountId) {
        this.id = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getFreeze() {
        return freeze;
    }

    public void setFreeze(Long freeze) {
        this.freeze = freeze;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Long getTotalExpend() {
        return totalExpend;
    }

    public void setTotalExpend(Long totalExpend) {
        this.totalExpend = totalExpend;
    }

    /**
     * 得到一个默认的初始化账户对象
     *
     * @return
     */
    public static Account newdefaultInstance() {
        Account account = new Account();
        account.setBalance(0l);
        account.setFreeze(0l);
        account.setTotalIncome(0l);
        account.setTotalExpend(0l);
        account.setVersion(0);
        return account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}