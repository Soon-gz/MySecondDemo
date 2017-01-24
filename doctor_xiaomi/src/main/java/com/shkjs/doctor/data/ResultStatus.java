package com.shkjs.doctor.data;

/**
 * Created by xiaohu on 2016/9/20.
 */
public enum ResultStatus {

    SUCCEED("成功"),
    FAIL("失败"),
    EXCEPTION("异常"),
    UNLOGIN("未登录"),
    NOTPERMISSION("没有权限"),
    INVALIDREQUEST("无效的请求");

    private String mark;

    ResultStatus(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

}
