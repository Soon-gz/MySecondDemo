package com.shkjs.doctor.http.response;


import com.shkjs.doctor.base.BaseResponse;

import java.util.List;

/**
 * Created by xiaohu on 2016/9/28.
 */

public class ListResponse<T> extends BaseResponse {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListResponse{" +
                "data=" + data +
                '}';
    }
}
