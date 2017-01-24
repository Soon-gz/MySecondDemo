package com.shkjs.doctor.bean;

/**
 * Created by Shuwen on 2016/10/8.
 */

public class SystemHelpBean {

    /**
     * content : string
     * createDate : 2016-10-08T02:20:07.335Z
     * dr : string
     * heat : 0
     * id : 0
     * titile : string
     * updateDate : 2016-10-08T02:20:07.335Z
     */

    private String content;
    private String createDate;
    private String dr;
    private int heat;
    private int id;
    private String titile;
    private String updateDate;

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

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
