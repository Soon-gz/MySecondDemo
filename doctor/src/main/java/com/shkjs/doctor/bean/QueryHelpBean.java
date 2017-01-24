package com.shkjs.doctor.bean;

/**
 * Created by Administrator on 2016/12/12.
 */

public class QueryHelpBean {

    /**
     * content : string
     * createDate : 2016-12-12T06:46:26.357Z
     * heat : 0
     * id : 0
     * titile : string
     * type : USER
     */

    private String content;
    private String createDate;
    private int heat;
    private int id;
    private String titile;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
