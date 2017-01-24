package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;

/**
 * Created by xiaohu on 2016/9/27.
 */

public class HelpMesage extends BaseModel {

    private long heat;//热度

    private String titile;//问题

    private String content;//回答

    public long getHeat() {
        return heat;
    }

    public void setHeat(long heat) {
        this.heat = heat;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
