package com.shkjs.doctor.data;

/**
 * Created by xiaohu on 2016/9/20.
 */
public enum ResponseStatusEnum {

    SUCCEED("SUCCEED"),
    FAIL("FAIL"),
    EXCEPTION("EXCEPTION"),
    UNLOGIN("UNLOGIN"),
    NOTPERMISSION("NOTPERMISSION");

    private String value;

    ResponseStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
