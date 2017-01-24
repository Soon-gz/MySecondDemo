package com.shkjs.doctor.bean;


import com.shkjs.doctor.data.AttachmentType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaohu on 2016/11/2.
 */

public class AttachmentContent implements Serializable {

    private AttachmentType type;
    private List<BodyContent> content;

    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    public List<BodyContent> getContent() {
        return content;
    }

    public void setContent(List<BodyContent> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AttachmentContent{" +
                "type='" + type + '\'' +
                ", content=" + content +
                '}';
    }
}
