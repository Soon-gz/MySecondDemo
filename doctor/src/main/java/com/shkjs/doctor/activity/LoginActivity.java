package com.shkjs.doctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.util.ActivityManager;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameET;
    private EditText userPwdET;
    private TextView loginBtn;
    private TextView helpTv;
    private TextView registerTv;
    private TextView text_right;

    private String userName = "";
    private String userPwd = "";
    private boolean isRegister;
    private long currentBackPressedTime = 0;                   // 点击返回键时间
    private static final int BACK_PRESSED_INTERVAL = 1000;    // 两次点击返回键时间间隔


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        loginBtn = (TextView) findViewById(R.id.login_submit_btn);
        helpTv = (TextView) findViewById(R.id.login_help_tv);
        registerTv = (TextView) findViewById(R.id.login_register_tv);
        text_right = (TextView) findViewById(R.id.text_ringht);
    }

    private void initData() {
        isRegister = getIntent().getBooleanExtra(Preference.ISREGISTER, false);
        if (!StringUtil.isEmpty(SharedPreferencesUtils.getString(Preference.USERNAME))){
            userName = SharedPreferencesUtils.getString(Preference.USERNAME);
            userPwd = SharedPreferencesUtils.getString(Preference.PASSWORD);
            userNameET.setText(userName);
            userPwdET.setText(userPwd);
        }
        if (isRegister) {
            userName = SharedPreferencesUtils.getString(Preference.USERNAME);
            userPwd = SharedPreferencesUtils.getString(Preference.PASSWORD);
            userNameET.setText(userName);
            userPwdET.setText(userPwd);
        }
        text_right.setText("联系客服");
    }

    private void initListener() {
        loginBtn.setOnClickListener(this);
        helpTv.setOnClickListener(this);
        registerTv.setOnClickListener(this);

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
                if (checkFinishAll()){
                    loginBtn.setEnabled(true);
                }else {
                    loginBtn.setEnabled(false);
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
                if (checkFinishAll()){
                    loginBtn.setEnabled(true);
                }else {
                    loginBtn.setEnabled(false);
                }
            }
        });

        text_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:"+"4008859120"));
                startActivity(intent);
            }
        });
    }

    public boolean checkFinishAll(){
        if (StringUtil.isEmpty(userNameET.getText().toString().trim())){
            return false;
        }

        if (StringUtil.isEmpty(userPwdET.getText().toString().trim())){
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit_btn:
                doLogin();
                break;
            case R.id.login_help_tv:
                startGetBackPwdActivity();
                break;
            case R.id.login_register_tv:
                startRegisterActivity();
                break;
        }
    }


    /**
     * 第一步，登录键医网账号
     */
    private void doLogin() {
        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(userPwd)) {
            ToastUtils.showToast(getResources().getString(R.string.username_and_password_hint));
            return;
        } else if (TextUtils.isEmpty(userName)) {
            ToastUtils.showToast(getResources().getString(R.string.username_hint));
            return;
        } else if (TextUtils.isEmpty(userPwd)) {
            ToastUtils.showToast(getResources().getString(R.string.userpwd_hint));
            return;
        } else if (!CheckUtils.isPhoneNumberValid(userName)) {
            ToastUtils.showToast(getResources().getString(R.string.username_right_hint));
            return;
        }
        LoginManager.loginServer(this,userName,userPwd,false);
    }




    private void startGetBackPwdActivity() {
        Intent intent = new Intent(LoginActivity.this, GetBackPwdActivity.class);
        startActivity(intent);
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * 连续两次点击返回键，回到桌面
     */
    @Override
    public void onBackPressed() {
        // 判断时间间隔
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            ToastUtils.showToast(getResources().getString(R.string.toast_main_back_hint));
        } else {
            //返回桌面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            ActivityManager.getInstance().finishAllActivity();
            startActivity(intent);
            System.exit(0);
        }
    }

}
