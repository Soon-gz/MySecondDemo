package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.data.em.PayType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.AliPayUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/10.
 * <p>
 * 充值
 */

public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.recharge_submit_ll)
    LinearLayout rechargeLL;
    @Bind(R.id.recharge_result_ll)
    LinearLayout resultLL;
    @Bind(R.id.recharge_money_other_ll)
    LinearLayout otherMoneyLL;
    @Bind(R.id.recharge_money_radiogroup)
    RadioGroup radioGroup;
    @Bind(R.id.recharge_money_first)
    RadioButton moneyFirst;
    @Bind(R.id.recharge_money_second)
    RadioButton moneySecond;
    @Bind(R.id.recharge_money_third)
    RadioButton moneyThird;
    @Bind(R.id.recharge_money_other)
    RadioButton moneyOther;
    @Bind(R.id.recharge_alipay_cb)
    CheckBox alipayCB;
    @Bind(R.id.recharge_submit_btn)
    Button submitBtn;
    @Bind(R.id.recharge_result_setting_pay_pwd)
    Button setPayPwdBtn;
    @Bind(R.id.recharge_result_continue_recharge)
    Button continueRechargeBtn;
    @Bind(R.id.recharge_money_other_et)
    EditText otherMoneyET;

    private Toolbar toolbar;

    private String money;
    private long balance;

    private boolean isShow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recharge);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.recharge_title);

        initData();
        initListener();

        if (balance < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        setVisibility(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.complete).setVisible(!isShow);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPayPwdIsExist();
    }

    private void initData() {
        balance = getIntent().getLongExtra(RechargeActivity.class.getSimpleName(), -1);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult();
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.complete:
                        setResult();
                        finish();
                        return true;
                }
                return false;
            }
        });

        submitBtn.setOnClickListener(this);
        setPayPwdBtn.setOnClickListener(this);
        continueRechargeBtn.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                money = "";
                switch (checkedId) {
                    case R.id.recharge_money_1:
                        money = "1";//测试用
                        otherMoneyLL.setVisibility(View.GONE);
                        otherMoneyET.setText("");
                        break;
                    case R.id.recharge_money_first:
                        money = "50000";
                        otherMoneyLL.setVisibility(View.GONE);
                        otherMoneyET.setText("");
                        break;
                    case R.id.recharge_money_second:
                        money = "100000";
                        otherMoneyLL.setVisibility(View.GONE);
                        otherMoneyET.setText("");
                        break;
                    case R.id.recharge_money_third:
                        money = "200000";
                        otherMoneyLL.setVisibility(View.GONE);
                        otherMoneyET.setText("");
                        break;
                    case R.id.recharge_money_other:
                        otherMoneyLL.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        radioGroup.check(R.id.recharge_money_first);

        otherMoneyET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(otherMoneyET)) {
                    long inputNum = Long.parseLong(TextUtils.getText(otherMoneyET));
                    if (inputNum <= 0) {
                        ToastUtils.showToast("请输入正确的金额");
                        money = "";
                    } else {
                        money = inputNum * 100 + "";
                    }
                }
            }
        });
    }

    private void setResult() {
        if (balance != 0) {
            Intent intent = new Intent();
            intent.putExtra(RechargeActivity.class.getSimpleName(), balance);
            setResult(RESULT_OK, intent);

        }
    }

    private void setVisibility(boolean isShow) {
        if (isShow) {
            rechargeLL.setVisibility(View.VISIBLE);
            resultLL.setVisibility(View.GONE);
            this.isShow = isShow;
        } else {
            rechargeLL.setVisibility(View.GONE);
            resultLL.setVisibility(View.VISIBLE);
            this.isShow = isShow;
        }
        invalidateOptionsMenu(); //重新绘制menu
    }

    private void submitRecharge() {
        if (TextUtils.isEmpty(money)) {

            ToastUtils.showToast("请输入充值金额");
            return;
        }
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e(getString(R.string.recharge_failed) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.recharge_failed));
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //                        setVisibility(false);
                        alipay(response.getData());
                    } else {
                        Logger.e(getString(R.string.recharge_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.recharge_failed));
                    }
                } else {
                    Logger.e(getString(R.string.recharge_failed));
                    ToastUtils.showToast(getString(R.string.recharge_failed));
                }
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.recharge(money, PayType.ALIPAY, callback);
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
                            setPayPwdBtn.setVisibility(View.GONE);
                        } else {
                            setPayPwdBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                    }
                } else {
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.queryPayPwdIsExist(callback);
    }

    private void setPayPassword() {
        Intent intent = new Intent(this, SettingPayPwdActivity.class);
        startActivity(intent);
        finish();
    }

    private void alipay(String orderInfo) {

        AliPayUtils.PayResultListener listener = new AliPayUtils.PayResultListener() {

            @Override
            public void onSuccess() {
                balance = balance + Long.parseLong(money);
                setVisibility(false);
            }

            @Override
            public void onFailed(String result) {
                ToastUtils.showToast(result);
            }
        };

        AliPayUtils.alipay(this, orderInfo, listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge_submit_btn:
                //TODO 实际需要真正支付成功后才向服务器提交该请求（支付阻塞）
                submitRecharge();
                break;
            case R.id.recharge_result_setting_pay_pwd:
                setResult();
                setPayPassword();
                break;
            case R.id.recharge_result_continue_recharge:
                setVisibility(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }
}
