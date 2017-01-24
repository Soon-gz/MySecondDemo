package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/9/25.
 */

public class SystemMessageBean {

    /**
     * action : RED_DOT
     * content : string
     * createDate : 2016-11-25T04:59:16.069Z
     * id : 0
     * roles : USER
     * status : string
     * userId : 0
     */

    private String action;
    private String content;
    private String createDate;
    private int id;
    private String roles;
    private String status;
    private int userId;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
