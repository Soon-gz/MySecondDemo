package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.MessageType;

/**
 * Created by xiaohu on 2016/8/18.
 */

public class Message extends BaseModel {
    /**
     * 消息ID
     */
    private long id;
    /**
     * 消息类型
     */
    private MessageType type;
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

    public Message(long id, MessageType type, String icon, String title, String content, long time) {
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
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

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type=" + type +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((Message) obj).getId() == this.getId()) {
            return true;
        }
        return super.equals(obj);
    }
}
