package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.ResponseStatusEnum;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindingAlipayActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.binding_alipay_certification_et)
    EditText binding_alipay_certification_et;
    @Bind(R.id.binding_alipay_getcertification_rv)
    RelativeLayout binding_alipay_getcertification_rv;
    @Bind(R.id.binding_alipay_send_certification_tv)
    TextView binding_alipay_send_certification_tv;
    @Bind(R.id.binding_alipay_send_timing_tv)
    TextView binding_alipay_send_timing_tv;
    @Bind(R.id.binding_alipay_phone_edit)
    EditText binding_alipay_phone_edit;

    private final int HANDLER_WHAT = 121;
    private final int HANDLER_END = 120;
    private boolean isEND = false;
    private String phoneNum = "";
    private String codeStr = "";
    private final String GET_CODE_TIME = "60";


    //点击获取验证码后，60S后才能再次点击
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == HANDLER_WHAT && !isEND) {
                codeStr = binding_alipay_send_certification_tv.getText().toString();
                if (codeStr.equals(getResources().getString(R.string.getcode_text))) {//当显示为获取验证码时，开始60倒计时，并且将按钮置为不可点开
                    binding_alipay_getcertification_rv.setClickable(false);
                    binding_alipay_send_certification_tv.setText(GET_CODE_TIME);
                    binding_alipay_send_certification_tv.setTextColor(getResources().getColor(R.color
                            .color_blue_0888ff));
                    binding_alipay_send_timing_tv.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(HANDLER_WHAT);
                } else {//当显示为时间时，每一秒数值减一
                    int time = Integer.parseInt(codeStr) - 1;
                    if (time == 0) {//倒计时为零时，将显示置为获取验证码，并且将按钮置为可点击
                        binding_alipay_send_certification_tv.setText(getResources().getString(R.string.getcode_text));
                        binding_alipay_getcertification_rv.setClickable(true);
                        binding_alipay_send_certification_tv.setTextColor(getResources().getColor(R.color.grey));
                        binding_alipay_send_timing_tv.setVisibility(View.GONE);
                    } else {
                        binding_alipay_send_certification_tv.setText(time + "");
                        mHandler.sendEmptyMessageDelayed(HANDLER_WHAT, 1000);
                    }
                }
            } else if (msg.what == HANDLER_END) {
                isEND = true;
                binding_alipay_send_certification_tv.setText(getResources().getString(R.string.getcode_text));
                binding_alipay_getcertification_rv.setClickable(true);
                binding_alipay_send_certification_tv.setTextColor(getResources().getColor(R.color.grey));
                binding_alipay_send_timing_tv.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_alipay);
        ButterKnife.bind(this);
        toptitle_tv.setText("绑定支付宝账号");
        initEvents();
    }

    private void initEvents() {
        if (DataCache.getInstance().getUserInfo() != null) {
            String phone = SharedPreferencesUtils.getString(Preference.USERNAME).substring(0, 3) + "****" +
                    SharedPreferencesUtils.getString(Preference.USERNAME).substring(7, 11);
            binding_alipay_phone_edit.setText(phone);
            binding_alipay_phone_edit.setEnabled(false);
        } else {
            binding_alipay_phone_edit.setEnabled(true);
        }
    }

    @OnClick({R.id.back_iv, R.id.binding_alipay_getcertification_rv, R.id.binding_alipay_next_tv})
    public void bindingOnClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.binding_alipay_getcertification_rv:
                getCode();
                break;
            case R.id.binding_alipay_next_tv:
                if ("".equals(binding_alipay_certification_et.getText().toString().trim())) {
                    ToastUtils.showToast("请输入验证码！");
                    break;
                }
                submitCode();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        //                    ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
        //                            .toast_permission_hint));
        //                }
        //            });
        //        }
    }


    /**
     * 获取验证码
     */
    private void getCode() {
        isEND = false;
        if (DataCache.getInstance().getUserInfo() != null) {
            phoneNum = SharedPreferencesUtils.getString(Preference.USERNAME);
        } else {
            phoneNum = binding_alipay_phone_edit.getText().toString().trim();
        }
        Log.i("tag00", "号码：" + phoneNum);
        binding_alipay_getcertification_rv.setClickable(false);
        if (!StringUtil.isEmpty(phoneNum)) {
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
            HttpProtocol.getShortMsgCode(phoneNum, callback,this);
        } else {
            ToastUtils.showToast("请填入您的有效手机号~");
        }

    }

    /**
     * 提交短信验证码
     */
    private void submitCode() {

        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResponseStatusEnum.SUCCEED.getValue())) {
                        ToastUtils.showToast("短信验证通过");
                        startActivity(new Intent(BindingAlipayActivity.this, BindingAlipayNextActivity.class));
                        finish();
                        return;
                    }
                }
                ToastUtils.showToast("短信验证失败，请输入正确的验证码!");
            }
        };
        callback.setMainThread(false);
        callback.setContext(this);
        HttpProtocol.veryfyMsgCode(binding_alipay_certification_et.getText().toString().trim(), callback);

    }
}
