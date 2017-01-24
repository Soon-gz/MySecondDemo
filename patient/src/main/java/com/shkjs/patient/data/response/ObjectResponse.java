package com.shkjs.patient.data.response;

import com.shkjs.patient.base.BaseResponse;

/**
 * Created by xiaohu on 2016/9/28.
 */

public class ObjectResponse<T> extends BaseResponse {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectResponse{" +
                "data=" + data +
                '}';
    }
}
