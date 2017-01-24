package com.shkjs.doctor.bean;

import com.shkjs.doctor.base.BaseModel;

import java.io.Serializable;

/**
 * Created by LENOVO on 2016/8/18.
 */

public class Message extends BaseModel implements Serializable {
    /**
     * 消息ID
     */
    private long id;
    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息图标
     */
    private String icon;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息时间
     */
    private long time;


    public Message() {
    }

    public Message(long id, int type, String icon, String title, String content, long time, int status) {
        this.id = id;
        this.type = type;
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
