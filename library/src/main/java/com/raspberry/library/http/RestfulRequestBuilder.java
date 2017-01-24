package com.raspberry.library.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.impl.RequestCallBuilder;

/**
 * Created by xiaohu on 2016/9/18.
 */
public class RestfulRequestBuilder extends RequestCallBuilder {

    @Override
    public Request.Builder builderPost(String url, String string) {
        //实现模拟PUT请求，MediaType必须为application/x-www-form-urlencoded
        RequestBody body = RequestBody.create(MediaType.parse(String.format("application/x-www-form-urlencoded; " +
                "charset=%s", mProtocolCharset)), string);

        return builderPost(url, body);
    }
}
