package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.UseStatus;

public class Permission extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -9145998778600661087L;

    private Long id;

    private String name;

    /**
     * 使用状态
     */
    private UseStatus status;

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

    public UseStatus getStatus() {
        return status;
    }

    public void setStatus(UseStatus status) {
        this.status = status;
    }
}
