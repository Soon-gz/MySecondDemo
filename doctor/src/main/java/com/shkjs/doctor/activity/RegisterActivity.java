package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.RegisterBean;
import com.shkjs.doctor.data.ResponseStatusEnum;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameET;
    private EditText userPwdET;
    private EditText codeET;
    private TextView getCodeBtn;
    private TextView submitCodeBtn;
    private CheckBox login_checkbox;
    private ImageView back_iv;
    private TextView topTitle;
    private TextView doctor_agreement;

    private String userName = "";
    private String userPwd = "";

    private final String GET_CODE_TIME = "60S";
    private String btnStr = "";
    private final int HANDLER_WHAT = 121;

    private final int HANDLER_END = 120;
    private boolean isEND = false;

    //点击获取验证码后，60S后才能再次点击
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == HANDLER_WHAT && !isEND) {
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
            } else if (msg.what == HANDLER_END) {
                isEND = true;
                getCodeBtn.setText(getResources().getString(R.string.getcode_text));
                getCodeBtn.setClickable(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findView();
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
        userNameET = (EditText) findViewById(R.id.register_username_et);
        userPwdET = (EditText) findViewById(R.id.register_userpwd_et);
        codeET = (EditText) findViewById(R.id.register_code_et);
        getCodeBtn = (TextView) findViewById(R.id.register_getcode_btn);
        submitCodeBtn = (TextView) findViewById(R.id.register_submit_btn);
        login_checkbox = (CheckBox) findViewById(R.id.login_checkbox);
        back_iv = (ImageView) findViewById(R.id.back_iv);
        topTitle = (TextView) findViewById(R.id.toptitle_tv);
        doctor_agreement = (TextView) findViewById(R.id.doctor_agreement);
    }


    public boolean checkFinishAll() {
        if (StringUtil.isEmpty(userNameET.getText().toString().trim())) {
            return false;
        }

        if (StringUtil.isEmpty(userPwdET.getText().toString().trim())) {
            return false;
        }

        if (StringUtil.isEmpty(codeET.getText().toString().trim())) {
            return false;
        }

        if (!login_checkbox.isChecked()) {
            return false;
        }
        return true;
    }

    private void initListener() {
        topTitle.setText("用户注册");

        getCodeBtn.setOnClickListener(this);
        submitCodeBtn.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        doctor_agreement.setOnClickListener(this);

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
                if (checkFinishAll()) {
                    submitCodeBtn.setEnabled(true);
                } else {
                    submitCodeBtn.setEnabled(false);
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
                if (checkFinishAll()) {
                    submitCodeBtn.setEnabled(true);
                } else {
                    submitCodeBtn.setEnabled(false);
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
                if (checkFinishAll()) {
                    submitCodeBtn.setEnabled(true);
                } else {
                    submitCodeBtn.setEnabled(false);
                }
            }
        });

        login_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkFinishAll()) {
                    submitCodeBtn.setEnabled(true);
                } else {
                    submitCodeBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_getcode_btn:
                getCode();
                break;
            case R.id.register_submit_btn:
                register();
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.doctor_agreement:
                startActivity(new Intent(this,DoctorAgreementActivity.class));
                break;
        }
    }


    /**
     * 获取验证码
     */
    private void getCode() {
        isEND = false;
        if (userName.isEmpty()) {
            ToastUtils.showToast(getResources().getString(R.string.username_hint));
            return;
        }
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                ToastUtils.showToast("网络异常，请检查网络或重试!");
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                if (HttpProtocol.checkStatus(response,code)) {
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
     * 向服务器注册
     */
    private void register() {
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

        RaspberryCallback<ObjectResponse<RegisterBean>> callback = new
                RaspberryCallback<ObjectResponse<RegisterBean>>() {
            @Override
            public void onSuccess(ObjectResponse<RegisterBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    SharedPreferencesUtils.put(Preference.USERNAME, userName);
                    SharedPreferencesUtils.put(Preference.PASSWORD, userPwd);
                    ToastUtils.showToast("注册成功，请登录。");
                    startLoginActivity();//注册成功回到登录界面，自动填入数据
                } else {
                    ToastUtils.showToast(response.getMsg());
                }

            }
        };
        HttpProtocol.register(userName, MD5Utils.encodeMD52(userPwd), codeET.getText().toString().trim(), callback,this);
    }

    private void startMainActivity() {
        MainActivity.startMainActivity(this);
        finish();
    }

    private void startLoginActivity() {
        ActivityManager.getInstance().finishActivity(LoginActivity.class);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(Preference.ISREGISTER, true);
        startActivity(intent);
        finish();
    }
}
