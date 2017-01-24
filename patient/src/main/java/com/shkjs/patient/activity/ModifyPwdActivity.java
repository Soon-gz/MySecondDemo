package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/25.
 * <p>
 * 修改密码
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.modify_user_password_ll)
    LinearLayout userPwdLL;
    @Bind(R.id.modify_pay_password_ll)
    LinearLayout payPwdLL;
    @Bind(R.id.retrieve_pay_password_ll)
    LinearLayout retrievePayPwdLL;

    private Toolbar toolbar;

    private boolean isPayPwd = false;
    private boolean isSecurity = false;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_pwd);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.modify_password);

        initData();
        initListener();

        //TODO 隐藏修改用户密码
        userPwdLL.setVisibility(View.GONE);

        checkPayPwdIsExist();
        querySecurityIsExist();

    }

    private void initData() {
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userPwdLL.setOnClickListener(this);
        payPwdLL.setOnClickListener(this);
        retrievePayPwdLL.setOnClickListener(this);
    }

    /**
     * 是否设置过支付密码
     */
    private void checkPayPwdIsExist() {
        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response
                            .getData()) {
                        isPayPwd = response.getData();
                        showView();
                    } else {
                    }
                } else {
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.queryPayPwdIsExist(callback);
    }

    /**
     * 是否设置过密保
     */
    private void querySecurityIsExist() {
        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response
                            .getData()) {
                        isSecurity = response.getData();
                        showView();
                    } else {
                    }
                } else {
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.querySecurityIsExist(callback);
    }

    private void showView() {
        if (isPayPwd && isSecurity) {
            retrievePayPwdLL.setVisibility(View.VISIBLE);
        } else {
            retrievePayPwdLL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.modify_user_password_ll:
                intent.setClass(ModifyPwdActivity.this, ModifyUserPwdActivity.class);
                break;
            case R.id.modify_pay_password_ll:
                intent.setClass(ModifyPwdActivity.this, ModifyPayPwdActivity.class);
                break;
            case R.id.retrieve_pay_password_ll:
                intent.setClass(ModifyPwdActivity.this, RetrievePayPwdActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
