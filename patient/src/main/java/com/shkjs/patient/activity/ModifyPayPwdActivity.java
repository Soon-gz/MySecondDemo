package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by xiaohu on 2016/10/28.
 * <p>
 * 修改支付密码
 */

public class ModifyPayPwdActivity extends BaseActivity {


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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_pay_password);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.modify_pay_password);

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
                    checkPayPassword();
                } else {
                    newPassword = str;
                    submitModifyPayPwd();
                }
            }
        });
    }

    /**
     * 校验旧支付密码
     */
    private void checkPayPassword() {

        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData()) {
                            isFirstInput = false;
                            initPayEditData(isFirstInput);
                            return;
                        }
                    }
                    ToastUtils.showToast("支付密码错误");
                    isFirstInput = true;
                    initPayEditData(isFirstInput);
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.checkPayPwd(MD5Utils.encodeMD52(password), callback);
    }

    /**
     * 提交修改支付密码
     */
    private void submitModifyPayPwd() {

        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData()) {
                            finish();
                            return;
                        }
                        ToastUtils.showToast("修改失败,请重试");
                        isFirstInput = true;
                        initPayEditData(isFirstInput);
                    }
                } else {
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.modifyPayPwd(MD5Utils.encodeMD52(newPassword), MD5Utils.encodeMD52(password), callback);
    }


    /**
     * 初始化提示信息
     *
     * @param isFirst 是否为首次输入
     */
    private void initPayEditData(boolean isFirst) {
        if (isFirst) {
            titleTV.setText("请输入旧密码，用于验证身份");
        } else {
            titleTV.setText("请输入新的支付密码");
        }
        payPwdEditText.clearText();
    }
}
