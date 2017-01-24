package com.shkjs.doctor.base;


import com.shkjs.doctor.bean.ClientVersion;

/**
 * Created by xiaohu on 2016/9/20.
 */
public class BaseResponse {

    /**
     * 说明
     */
    private String msg;
    /**
     * 执行结果
     * FAIL:失败  SUCCEED:成功  EXCEPTION:异常
     */
    private String status;
    private String description;
    private ClientVersion clientVersion;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(ClientVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", clientVersion=" + clientVersion +
                '}';
    }

}
