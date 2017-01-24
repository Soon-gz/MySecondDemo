package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.register_username_et)
    EditText userNameET;
    @Bind(R.id.register_userpwd_et)
    EditText userPwdET;
    @Bind(R.id.register_code_et)
    EditText codeET;
    @Bind(R.id.register_getcode_tv)
    TextView codeTV;
    @Bind(R.id.register_submit_btn)
    Button submitBtn;
    @Bind(R.id.register_cb)
    CheckBox checkBox;
    @Bind(R.id.register_agreement_tv)
    TextView agreementTV;

    private Toolbar toolbar;

    private String userName = "";
    private String userPwd = "";

    private final String GET_CODE_TIME = "60S";
    private String btnStr = "";
    private final int HANDLER_WHAT = 121;

    //点击获取验证码后，60S后才能再次点击
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == HANDLER_WHAT) {
                btnStr = codeTV.getText().toString().trim();
                if (btnStr.equals(getResources().getString(R.string.getcode_text))) {//当显示为获取验证码时，开始60倒计时，并且将按钮置为不可点开
                    codeTV.setClickable(false);
                    codeTV.setText(GET_CODE_TIME);
                    mHandler.sendEmptyMessage(HANDLER_WHAT);
                } else if (btnStr.contains("S")) {//当显示为时间时，每一秒数值减一
                    int time = Integer.parseInt((btnStr.split("S")[0])) - 1;
                    if (time == 0) {//倒计时为零时，将显示置为获取验证码，并且将按钮置为可点击
                        codeTV.setText(getResources().getString(R.string.getcode_text));
                        codeTV.setClickable(true);
                    } else {
                        codeTV.setText(time + "S");
                        mHandler.sendEmptyMessageDelayed(HANDLER_WHAT, 1000);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.register_btn_text);

        queryUserInfo = false;

        initData();
        initListener();
        setEnable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        //当Android版本大于等于23时，动态授权
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

    private void initData() {

    }

    private void initListener() {
        codeTV.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        agreementTV.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                setEnable();
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
                setEnable();
            }
        });

        codeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnable();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnable();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_getcode_tv:
                getCode();
                break;
            case R.id.register_submit_btn:
                register();
                break;
            case R.id.register_agreement_tv:
                Intent intent = new Intent(RegisterActivity.this, UserAgreementActivity.class);
                startActivity(intent);
                break;
            default:
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
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        mHandler.sendEmptyMessage(HANDLER_WHAT);
                        return;
                    }
                }
                ToastUtils.showToast("获取验证码失败，请重试!");
            }
        };
        callback.setMainThread(false);
        callback.setContext(this);
        HttpProtocol.getShortMsgCode(userName, callback);
    }


    /**
     * 向服务器注册
     */
    private void register() {
        if (userName.isEmpty() && userPwd.isEmpty()) {
            ToastUtils.showToast2(getResources().getString(R.string.username_and_password_hint), Gravity.CENTER);
            return;
        } else if (userName.isEmpty()) {
            ToastUtils.showToast2(getResources().getString(R.string.username_hint), Gravity.CENTER);
            return;
        } else if (userPwd.isEmpty()) {
            ToastUtils.showToast2(getResources().getString(R.string.userpwd_hint), Gravity.CENTER);
            return;
        } else if (codeET.getText().toString().trim().isEmpty()) {
            ToastUtils.showToast2(getResources().getString(R.string.usercode_hint), Gravity.CENTER);
            return;
        } else if (!CheckUtils.isPhoneNumberValid(userName)) {
            ToastUtils.showToast2(getResources().getString(R.string.username_right_hint), Gravity.CENTER);
            return;
        }

        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("注册成功，请登录。");
                        SharedPreferencesUtils.put(MyApplication.USER_NAME, userName);
                        SharedPreferencesUtils.put(MyApplication.USER_PWD, userPwd);
                        startLoginActivity();//注册成功回到登录界面，自动填入数据
                    } else {
                        ToastUtils.showToast2(response.getMsg(), Gravity.CENTER);
                    }
                    return;
                }
                ToastUtils.showToast2(getResources().getString(R.string.register_fail) + "请重试", Gravity.CENTER);
            }
        };
        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);
        HttpProtocol.register(userName, MD5Utils.encodeMD52(userPwd), codeET.getText().toString().trim(), callback);
    }

    private void startMainActivity() {
        MainActivity.startMainActivity(this);
        finish();
    }

    private void startLoginActivity() {
        ActivityManager.getInstance().finishActivity(LoginActivity.class);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(Preference.IS_REGISTER, true);
        startActivity(intent);
        finish();
    }

    private void setEnable() {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd) || TextUtils.isEmpty(codeET) || !checkBox
                .isChecked()) {
            submitBtn.setEnabled(false);
        } else {
            submitBtn.setEnabled(true);
        }
    }
}
