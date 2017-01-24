package com.shkjs.patient.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaohu on 2016/9/21.
 */
public class ArchiveMessage implements Serializable {

    List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
