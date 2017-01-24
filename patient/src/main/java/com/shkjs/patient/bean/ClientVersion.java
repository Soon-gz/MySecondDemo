package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/9/20.
 */
public class ClientVersion implements Serializable {

    /**
     * 客户端版本号
     */
    private String code;
    /**
     * 客户端版本说明
     */
    private String describe;
    /**
     * 客户端版本类型
     */
    private String versionType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    @Override
    public String toString() {
        return "code:" + code + "\ndescribe:" + describe + "\nversionType:" + versionType;
    }
}
