package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.data.ResponseStatusEnum;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.impl.UiCallback;

public class GetBackPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameET;
    private EditText userPwdET;
    private EditText codeET;
    private TextView getCodeBtn;
    private TextView submitBtn;
    private TextView topTitle;
    private ImageView back_iv;

    private String userName = "";
    private String userPwd = "";

    private final int HANDLER_WHAT = 121;
    private final String GET_CODE_TIME = "60S";
    private String btnStr = "";

    //点击获取验证码后，60S后才能再次点击
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == HANDLER_WHAT) {
                btnStr = getCodeBtn.getText().toString().trim();
                if (btnStr.equals(getResources().getString(R.string.getcode_text))) {//当显示为获取验证码时，开始60倒计时，并且将按钮置为不可点开
                    getCodeBtn.setClickable(false);
                    getCodeBtn.setText(GET_CODE_TIME);
                    mHandler.sendEmptyMessage(HANDLER_WHAT);
                } else if (btnStr.contains("S")) {//当显示为时间时，每一秒数值减一
                    int time = Integer.parseInt((btnStr.split("S")[0])) - 1;
                    if (time == 0) {//倒计时为零时，将显示置为获取验证码，并且将按钮置为可点击
                        getCodeBtn.setText(getResources().getString(R.string.getcode_text));
                        getCodeBtn.setClickable(true);
                    } else {
                        getCodeBtn.setText(time + "S");
                        mHandler.sendEmptyMessageDelayed(HANDLER_WHAT, 1000);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getback_pwd);

        findView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当Android版本大于等于23时，动态授权
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
        // .READ_SMS,
        //                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
        // .permission
        //                            .WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
        //
        //                @Override
        //                public void onGranted() {
        //
        //                }
        //
        //                @Override
        //                public void onDenied(List<String> permissions) {
        //                }
        //            });
        //        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findView() {
        userNameET = (EditText) findViewById(R.id.getback_pwd_username_et);
        userPwdET = (EditText) findViewById(R.id.getback_pwd_password_et);
        codeET = (EditText) findViewById(R.id.getback_pwd_code_et);
        getCodeBtn = (TextView) findViewById(R.id.getback_pwd_getcode_btn);
        submitBtn = (TextView) findViewById(R.id.getback_pwd_submit_code_btn);
        back_iv = (ImageView) findViewById(R.id.back_iv);
        topTitle = (TextView) findViewById(R.id.toptitle_tv);
    }

    private void initData() {
        topTitle.setText("重置密码");
        //        userPwdET.setVisibility(View.VISIBLE);
        //        submitBtn.setText(getString(R.string.submit_text));
    }

    private void initListener() {
        getCodeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        back_iv.setOnClickListener(this);

        userNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userPwdET.setText("");//用户名变化时，密码置空
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //得到用户名
                userName = userNameET.getText().toString().trim();
                if (finishAll()) {
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
                }
            }
        });

        userPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //得到用户密码
                userPwd = userPwdET.getText().toString().trim();
                if (finishAll()) {
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
                }
            }
        });

        codeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (finishAll()) {
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
                }
            }
        });
    }

    public boolean finishAll() {
        if (StringUtil.isEmpty(userNameET.getText().toString().trim())) {
            return false;
        }

        if (StringUtil.isEmpty(userPwdET.getText().toString().trim())) {
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getback_pwd_getcode_btn:
                getCode();
                break;
            case R.id.getback_pwd_submit_code_btn:
                getBackPwd();
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        if (userName.isEmpty()) {
            ToastUtils.showToast2(getResources().getString(R.string.username_hint), Gravity.CENTER);
            return;
        } else if (!CheckUtils.isPhoneNumberValid(userName)) {
            ToastUtils.showToast2(getResources().getString(R.string.username_right_hint), Gravity.CENTER);
            return;
        }
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                ToastUtils.showToast("网络异常，请检查网络或重试!");
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResponseStatusEnum.SUCCEED.getValue())) {
                        mHandler.sendEmptyMessage(HANDLER_WHAT);
                        return;
                    }
                }
                ToastUtils.showToast("获取验证码失败，请重试!");
            }
        };
        callback.setMainThread(false);
        callback.setContext(this);
        HttpProtocol.getShortMsgCode(userName, callback,this);
    }


    /**
     * 找回密码
     */
    private void getBackPwd() {
        if (userName.isEmpty() && userPwd.isEmpty()) {
            ToastUtils.showToast(getResources().getString(R.string.username_and_password_hint));
            return;
        } else if (userName.isEmpty()) {
            ToastUtils.showToast(getResources().getString(R.string.username_hint));
            return;
        } else if (userPwd.isEmpty()) {
            ToastUtils.showToast(getResources().getString(R.string.userpwd_hint));
            return;
        } else if (codeET.getText().toString().trim().isEmpty()) {
            ToastUtils.showToast(getResources().getString(R.string.usercode_hint));
            return;
        } else if (!CheckUtils.isPhoneNumberValid(userName)) {
            ToastUtils.showToast(getResources().getString(R.string.username_right_hint));
            return;
        }

        UiCallback callback = new UiCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                Logger.d("code:" + code);
                if (code == 200) {
                    if (response.getStatus().equals(ResponseStatusEnum.SUCCEED.getValue())) {
                        startLoginActivity();
                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                    return;
                }
                ToastUtils.showToast(getString(R.string.getback_pwd_fail) + ",请重试");

            }
        };

        HttpProtocol.findPassword(userName, MD5Utils.encodeMD52(userPwd), codeET.getText().toString().trim(), callback,this);
    }

    private void startLoginActivity() {
        ActivityManager.getInstance().finishActivity(LoginActivity.class);
        Intent intent = new Intent(GetBackPwdActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
