package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

import java.util.List;

public class User extends BaseModel {

    public static final String SESSIONUSER = "sessionUser";

    private static final long serialVersionUID = 1809235591818393547L;

    private Long id;

    private String userName;

    private String password;


    /**
     * 用户的角色
     */
    private List<Role> roles;

    public User() {

    }

    public User(User u) {
        this.id = u.id;
        this.userName = u.userName;
        this.password = u.password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}