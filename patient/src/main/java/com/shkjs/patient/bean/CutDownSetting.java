package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.CutDownType;

public class CutDownSetting extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = 482137966794387799L;

    private Long id;
    //24小时以内
    private Long innerOneDay;
    //24小时以外
    private Long overOneDay;
    private CutDownType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInnerOneDay() {
        return innerOneDay;
    }

    public void setInnerOneDay(Long innerOneDay) {
        this.innerOneDay = innerOneDay;
    }

    public Long getOverOneDay() {
        return overOneDay;
    }

    public void setOverOneDay(Long overOneDay) {
        this.overOneDay = overOneDay;
    }

    public CutDownType getType() {
        return type;
    }

    public void setType(CutDownType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CutDownSetting{" +
                "id=" + id +
                ", innerOneDay=" + innerOneDay +
                ", overOneDay=" + overOneDay +
                ", type=" + type +
                '}';
    }
}
