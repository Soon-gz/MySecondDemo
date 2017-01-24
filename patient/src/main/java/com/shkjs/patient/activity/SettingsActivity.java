package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.nim.chat.login.LoginHelper;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.base.BaseResponse;
import com.shkjs.patient.bean.SecurityQuestion;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p/>
 * 设置
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    @Bind(R.id.setting_cb)
    CheckBox checkBox;
    @Bind(R.id.setting_pay_password_ll)
    LinearLayout setPayPwdLL;
    @Bind(R.id.setting_modify_password_ll)
    LinearLayout modifyPwdLL;
    @Bind(R.id.setting_logout_ll)
    LinearLayout logoutLL;
    @Bind(R.id.setting_pay_password_tv)
    TextView setPayPwdTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.setting_text);

        initData();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPayPwdIsExist();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveVoiceSetting(checkBox.isChecked());
    }

    private void initData() {
        checkBox.setChecked(SharedPreferencesUtils.getBoolean(MyApplication.VOICE_SETTING));
    }

    private void initListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setPayPwdLL.setOnClickListener(this);
        modifyPwdLL.setOnClickListener(this);
        logoutLL.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveVoiceSetting(b);
            }
        });
    }

    private void saveVoiceSetting(boolean open) {
        SharedPreferencesUtils.put(MyApplication.VOICE_SETTING, open);
    }

    /**
     * 修改密码
     */
    private void modifyPwd() {
        Intent intent = new Intent(this, ModifyPwdActivity.class);
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void logout() {
        RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {

            }

            @Override
            public void onSuccess(BaseResponse response, int code) {

            }
        };

        HttpProtocol.logout(callback);
        LoginHelper.logout();
        SharedPreferencesUtils.put(MyApplication.IS_AUTO_LOGIN, false);//取消自动登录
        DataCache.getInstance().cleanCache();//清除缓存数据、状态

        ActivityManager.getInstance().finishAllActivity();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData()) {
                            querySecurityIsExist();
                        } else {
                            setPayPwdLL.setVisibility(View.VISIBLE);
                            modifyPwdLL.setVisibility(View.GONE);
                            setPayPwdTV.setText(getString(R.string.setting_pay_password));
                        }
                    }
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
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData()) {
                            setPayPwdLL.setVisibility(View.GONE);
                        } else {
                            setPayPwdLL.setVisibility(View.VISIBLE);
                            setPayPwdTV.setText(getString(R.string.setting_security_question));
                        }
                    }
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.querySecurityIsExist(callback);
    }

    /**
     * 是否设置过密保
     */
    private void querySecurityQuestions() {
        RaspberryCallback<ListResponse<SecurityQuestion>> callback = new
                RaspberryCallback<ListResponse<SecurityQuestion>>() {
            @Override
            public void onSuccess(ListResponse<SecurityQuestion> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        if (null != response.getData()) {
                            setPayPwdLL.setVisibility(View.GONE);
                        } else {
                            setPayPwdLL.setVisibility(View.VISIBLE);
                            setPayPwdTV.setText(getString(R.string.setting_security_question));
                        }
                    }
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.querySecurityQuestion(callback);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.setting_modify_password_ll:
                modifyPwd();
                break;
            case R.id.setting_pay_password_ll:
                Intent intent = new Intent();
                if (TextUtils.getText(setPayPwdTV).equals(getString(R.string.setting_security_question))) {//设置密保
                    intent.setClass(SettingsActivity.this, SettingSecurityQuestionActivity.class);
                } else {//设置支付密码
                    intent.setClass(SettingsActivity.this, SettingPayPwdActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.setting_logout_ll:
                CustomAlertDialog.dialogExSureCancel("确认退出？", SettingsActivity.this, new CustomAlertDialog
                        .OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        logout();
                    }
                });
                break;
            default:
                break;
        }

    }
}
