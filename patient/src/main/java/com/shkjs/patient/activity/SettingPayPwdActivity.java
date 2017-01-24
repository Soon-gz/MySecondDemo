package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.PayPwdEditText;
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
 * Created by xiaohu on 2016/10/11.
 */

public class SettingPayPwdActivity extends BaseActivity {

    @Bind(R.id.password_tv)
    TextView titleTV;
    @Bind(R.id.password_ppet)
    PayPwdEditText payPwdEditText;

    private Toolbar toolbar;

    private String password;
    private String newPassword;
    private boolean isFirstInput = true;

    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_pay_pwd);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.setting_pay_password);

        initData();
        initListener();
        initPayEditData(isFirstInput);
    }

    private void initData() {

        width = DisplayUtils.getScreenWidth(this);
        width = width - 2 * DisplayUtils.dip2px(this, 14);
        height = width / 6;

        ViewGroup.LayoutParams params = payPwdEditText.getLayoutParams();
        params.width = width;
        params.height = height;

        payPwdEditText.setLayoutParams(params);
        payPwdEditText.initStyle(R.drawable.shape_background, 6, 1f, R.color.green_d2d2d2, R.color.black_231815, 20);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        payPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener() {
            @Override
            public void onFinish(String str) {
                if (isFirstInput) {
                    password = str;
                    isFirstInput = false;
                    initPayEditData(isFirstInput);
                } else {
                    newPassword = str;
                    if (checkPwd()) {
                        submitPayPwd();
                    } else {
                        ToastUtils.showToast("两次输入密码不同，请重新输入");
                        isFirstInput = true;
                        initPayEditData(isFirstInput);
                    }
                }
            }
        });
    }

    /**
     * 提交支付密码
     */
    private void submitPayPwd() {

        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData()) {
                            Intent intent = new Intent(SettingPayPwdActivity.this, SettingSecurityQuestionActivity
                                    .class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                }
                ToastUtils.showToast("设置失败，请重试");
                isFirstInput = true;
                initPayEditData(isFirstInput);
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.initPayPwd(MD5Utils.encodeMD52(password), callback);
    }

    /**
     * 校验两次输入密码是否相同
     *
     * @return
     */
    private boolean checkPwd() {
        if (password.equals(newPassword)) {
            return true;
        }
        return false;
    }

    /**
     * 初始化提示信息
     *
     * @param isFirst 是否为首次输入
     */
    private void initPayEditData(boolean isFirst) {
        if (isFirst) {
            titleTV.setText("请输入6位数字作为您的支付密码");
        } else {
            titleTV.setText("请再次输入您的支付密码");
        }
        payPwdEditText.clearText();
    }
}