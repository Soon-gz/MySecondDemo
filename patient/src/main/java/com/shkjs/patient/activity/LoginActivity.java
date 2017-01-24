package com.shkjs.patient.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameET;
    private EditText userPwdET;
    private Button loginBtn;
    private TextView helpTv;
    private TextView registerTv;
    private TextView titleTv;
    private TextView callPhoneTV;

    private String userName = "";
    private String userPwd = "";
    private boolean isRegister;
    private boolean isAuto;


    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.class.getSimpleName(), true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        queryUserInfo = false;

        findView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findView() {
        userNameET = (EditText) findViewById(R.id.login_username_et);
        userPwdET = (EditText) findViewById(R.id.login_userpwd_et);
        loginBtn = (Button) findViewById(R.id.login_submit_btn);
        helpTv = (TextView) findViewById(R.id.login_help_tv);
        registerTv = (TextView) findViewById(R.id.login_register_tv);
        titleTv = (TextView) findViewById(R.id.login_top_title_tv);
        callPhoneTV = (TextView) findViewById(R.id.call_phone);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            titleTv.setPadding(titleTv.getPaddingLeft(), statusBarHetght, titleTv.getPaddingRight(), titleTv
//                    .getPaddingBottom());
//            callPhoneTV.setPadding(callPhoneTV.getPaddingLeft(), statusBarHetght, callPhoneTV.getPaddingRight(),
//                    callPhoneTV.getPaddingBottom());
//        }
    }

    private void initData() {
        isRegister = getIntent().getBooleanExtra(Preference.IS_REGISTER, false);
        isAuto = getIntent().getBooleanExtra(LoginActivity.class.getSimpleName(), false);
        if (isRegister || isAuto) {
            userName = SharedPreferencesUtils.getString(MyApplication.USER_NAME);
            userPwd = SharedPreferencesUtils.getString(MyApplication.USER_PWD);
            userNameET.setText(userName);
            userPwdET.setText(userPwd);
        }
        //        userNameET.setText(userName);
        //        userPwdET.setText(userPwd);
    }

    private void initListener() {
        loginBtn.setOnClickListener(this);
        helpTv.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        callPhoneTV.setOnClickListener(this);

        userNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //用户名变化时，密码置空
                userPwdET.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //得到用户名
                userName = userNameET.getText().toString().trim();
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
                if (TextUtils.isEmpty(userPwd)) {
                    loginBtn.setEnabled(false);
                } else {
                    if (userPwd.length() >= 6) {
                        loginBtn.setEnabled(true);
                    } else {
                        loginBtn.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit_btn:
                //                startMainActivity();
                doLogin();
                //                loginNim();
                break;
            case R.id.login_help_tv:
                startGetBackPwdActivity();
                break;
            case R.id.login_register_tv:
                startRegisterActivity();
                break;
            case R.id.call_phone:
                callPhone();
                break;
            default:
                break;
        }
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + "400-8859-120");
            intent.setData(data);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showToast("您的设备没有拨号功能");
        }
    }

    private void doLogin() {
        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(userPwd)) {
            ToastUtils.showToast2(getResources().getString(R.string.username_and_password_hint), Gravity.CENTER);
            return;
        } else if (TextUtils.isEmpty(userName)) {
            ToastUtils.showToast2(getResources().getString(R.string.username_hint), Gravity.CENTER);
            return;
        } else if (TextUtils.isEmpty(userPwd)) {
            ToastUtils.showToast2(getResources().getString(R.string.userpwd_hint), Gravity.CENTER);
            return;
        } else if (!CheckUtils.isPhoneNumberValid(userName)) {
            ToastUtils.showToast2(getResources().getString(R.string.username_right_hint), Gravity.CENTER);
            return;
        }
        LoginManager.loginServer(this, userName, userPwd, false);

        //        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
        //            @Override
        //            public void onFailure(Request request, Response response, Exception e) {
        //                Logger.e(e.getMessage());
        //                ToastUtils.showToast2(getResources().getString(R.string.login_fail) + ",请检查网络", Gravity
        // .CENTER);
        //            }
        //
        //            @Override
        //            public void onSuccess(ObjectResponse<String> response, int code) {
        //                if (code == HttpBase.SUCCESS) {
        //                    if (response.getStatus().equals(ResponseStatusEnum.SUCCEED.getValue())) {
        //                        SharedPreferencesUtils.put(Preference.USER_NAME, userName);
        //                        SharedPreferencesUtils.put(Preference.USER_PWD, userPwd);
        //                        SharedPreferencesUtils.put(Preference.NIM_PASSWORD, userPwd);
        //                        SharedPreferencesUtils.put(Preference.IS_AUTO_LOGIN, true);//自动登录
        //                        DataCache.getInstance().queryUserInfo();//登录成功，查询用户信息
        //                        startMainActivity();
        //                    } else if (response.getStatus().equals(ResponseStatusEnum.EXCEPTION.getValue())) {
        //                        ToastUtils.showToast2(getResources().getString(R.string.login_fail) + response
        // .getMsg(),
        //                                Gravity.CENTER);
        //                    } else {
        //                        ToastUtils.showToast2(getResources().getString(R.string.login_fail) + response
        // .getMsg(),
        //                                Gravity.CENTER);
        //                    }
        //                } else {
        //                    ToastUtils.showToast2(getString(R.string.login_fail) + code, Gravity.CENTER);
        //                }
        //            }
        //        };
        //        callback.setContext(this);
        //        callback.setMainThread(true);
        //        callback.setCancelable(false);
        //        HttpProtocol.login(userName, userPwd, callback);
    }

    private void startMainActivity() {
        MainActivity.startMainActivity(this);
        finish();
    }

    private void startGetBackPwdActivity() {
        Intent intent = new Intent(LoginActivity.this, RetrievePwdActivity.class);
        startActivity(intent);
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

}
