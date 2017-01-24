package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.AgreeStatus;

public class UserGroupMember extends BaseModel {

    private static final long serialVersionUID = -5397398685994446155L;

    private Long id;

    private UserGroup userGroup;

    private User user;

    private AgreeStatus agreeStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgreeStatus getAgreeStatus() {
        return agreeStatus;
    }

    public void setAgreeStatus(AgreeStatus agreeStatus) {
        this.agreeStatus = agreeStatus;
    }


    /**
     * 得到一个默认的 GroupMember
     *
     * @return
     */
    public static UserGroupMember newDefaultUserGroupMember() {
        UserGroupMember userGroupMember = new UserGroupMember();
        userGroupMember.setAgreeStatus(AgreeStatus.ASK);
        return userGroupMember;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}