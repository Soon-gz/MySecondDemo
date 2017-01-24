package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

public class UserGroup extends BaseModel {

    private static final long serialVersionUID = -2687929941200780294L;

    private Long id;

    private String name;

    private UserInfo user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }


}