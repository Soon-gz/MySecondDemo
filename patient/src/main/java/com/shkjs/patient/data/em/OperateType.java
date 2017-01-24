package com.shkjs.patient.data.em;

public enum OperateType {
    ADD("添加"), DEL("删除"), MODIFY("修改"), QUIT("退出");

    private String mark;

    OperateType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
