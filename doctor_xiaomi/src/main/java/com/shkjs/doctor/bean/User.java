package com.shkjs.doctor.bean;

/**
 * Created by LENOVO on 2016/8/15.
 */

public class User {

    private long id;
    private String userName;
    private String password;
    private long roleId;

    public User() {
    }

    public User(long id, String userName, String password, long roleId) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.roleId = roleId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "id:" + this.id + "\nuserName:" + this.userName + "\npassword:" + this.password + "\nroleId:" + this
                .roleId;
    }
}
