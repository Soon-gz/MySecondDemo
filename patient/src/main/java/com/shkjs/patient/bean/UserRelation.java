package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.RelationType;

public class UserRelation extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -5755920178695449998L;

    private Long id;

    private User keyUser;

    private User relationUser;

    private RelationType type;

    private String nickName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    public User getRelationUser() {
        return relationUser;
    }

    public void setRelationUser(User relationUser) {
        this.relationUser = relationUser;
    }

    public User getKeyUser() {
        return keyUser;
    }

    public void setKeyUser(User keyUser) {
        this.keyUser = keyUser;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}