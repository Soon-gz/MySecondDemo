package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.base.BaseResponse;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/28.
 * <p>
 * 修改用户密码
 * <p>
 * TODO 由于现在不需要修改用户密码，故后期再实现
 */

public class ModifyUserPwdActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.modify_pwd_oldpwd_et)
    EditText oldPwdET;
    @Bind(R.id.modify_pwd_newpwd_et)
    EditText newPwdET;
    @Bind(R.id.modify_pwd_submit_btn)
    Button submitBtn;

    private Toolbar toolbar;

    private String oldPwd = "";
    private String newPwd = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_user_password);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.modify_user_password);

        initData();
        initListener();
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
        submitBtn.setOnClickListener(this);

        oldPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                oldPwd = oldPwdET.getText().toString().trim();
            }
        });

        newPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newPwd = newPwdET.getText().toString().trim();
            }
        });
    }

    private void modifyPwd() {
        if (oldPwd.isEmpty()) {
            ToastUtils.showToast(getString(R.string.userpwd_hint));
            return;
        } else if (newPwd.isEmpty()) {
            ToastUtils.showToast(getString(R.string.user_newpwd_hint));
            return;
        } else if (oldPwd.equals(newPwd)) {
            ToastUtils.showToast(getString(R.string.different_password));
            return;
        }

        RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("ModifyPwd:", getString(R.string.modify_password_fail) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.modify_password_fail));
            }

            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                //TODO 成功之后回到登录界面,需要结束其他服务之类的
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ActivityManager.getInstance().finishAllActivity();
                        startActivity(new Intent(ModifyUserPwdActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Logger.e("ModifyPwd", getString(R.string.modify_password_fail) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.modify_password_fail) + response.getMsg());
                    }
                } else {
                    Logger.e("ModifyPwd", getString(R.string.modify_password_fail));
                    ToastUtils.showToast(getString(R.string.modify_password_fail));
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);
        HttpProtocol.modifyPassword(MD5Utils.encodeMD52(oldPwd), MD5Utils.encodeMD52(newPwd), callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modify_pwd_submit_btn:
                modifyPwd();
                break;
            default:
                break;
        }
    }
}
