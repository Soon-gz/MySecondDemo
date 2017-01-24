package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Message;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/21.
 * <p>
 * 消息详情界面
 */

public class MessageDetailActivity extends BaseActivity {

    private Toolbar toolbar;
    @Bind(R.id.message_content_tv)
    TextView contentTV;

    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_detail);

        //绑定控件
        ButterKnife.bind(this);

        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.message_detail);

        initData();
        initListener();

        if (null == message) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        uploadPushMessageRead();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        message = (Message) getIntent().getSerializableExtra(MessageDetailActivity.class.getSimpleName());
        if (null != message) {
            contentTV.setText(message.getContent());
        }
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 上传消息为已读
     */
    private void uploadPushMessageRead() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
            }
        };
        callback.setMainThread(false);

        HttpProtocol.uploadPushMessageReadStatus(message.getId(), callback);
    }
}
