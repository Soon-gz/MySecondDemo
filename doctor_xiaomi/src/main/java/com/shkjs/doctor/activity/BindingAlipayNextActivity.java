package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.util.AudioHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindingAlipayNextActivity extends BaseActivity {

    @Bind(R.id.binding_next_name_edittext_tv)
    EditText binding_next_name_edittext_tv;
    @Bind(R.id.binding_next_phone_edittext_tv)
    EditText binding_next_phone_edittext_tv;
    @Bind(R.id.binding_next_phone_edittext_again)
    EditText binding_next_phone_edittext_again;
    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;

    private RaspberryCallback<BaseResponse>callback;
    private String bandCard;
    private String parthen = "1\\d{10}|^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_alipay_next);
        ButterKnife.bind(this);
        toptitle_tv.setText("绑定支付宝账户");

        initListener();
    }

    private void initListener() {
        if (DataCache.getInstance().getUserInfo() != null){
            binding_next_name_edittext_tv.setEnabled(false);
            binding_next_name_edittext_tv.setText(DataCache.getInstance().getUserInfo().getName());
        }else {
            binding_next_name_edittext_tv.setEnabled(true);
        }
        callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    startActivity(new Intent(BindingAlipayNextActivity.this,AliBindSucessFailActivity.class).putExtra(Preference.SUCCESSED_FAIL,Preference.SUCCESSED));
                    finish();
                }else {
                    startActivity(new Intent(BindingAlipayNextActivity.this,AliBindSucessFailActivity.class).putExtra(Preference.SUCCESSED_FAIL,Preference.FAIL));
                }
            }
        };
        callback.setShowDialog(true);
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
    }

    @OnClick({R.id.back_iv,R.id.binding_alipay_final_tv})
    public void bandingOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                CustomAlertDialog.dialogExSureCancel("确认退出？当前未完成绑定，退出后需重新验证手机号码。", this, new CustomAlertDialog.OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        finish();
                    }
                });
                break;
            case R.id.binding_alipay_final_tv:
                if (isFinishedAll()){
                    HttpProtocol.bindCard(callback,bandCard,Preference.BANKSTATUS,this);
                }
                break;
        }
    }

    //检查是否完成了所有内容
    public boolean isFinishedAll(){
        if (StringUtil.isEmpty(binding_next_name_edittext_tv.getText().toString().trim())){
            ToastUtils.showToast("请填写您的真实姓名");
            return false;
        }
        if (StringUtil.isEmpty(binding_next_phone_edittext_tv.getText().toString().trim()) || !isPhoneNumber(binding_next_phone_edittext_tv.getText().toString().trim())){
            ToastUtils.showToast("请填写您正确的手机号或邮箱");
            return false;
        }
        if (StringUtil.isEmpty(binding_next_phone_edittext_again.getText().toString().trim()) ||  !isPhoneNumber(binding_next_phone_edittext_tv.getText().toString().trim())){
            ToastUtils.showToast("请再次确认您的手机号或邮箱");
            return false;
        }
        if (!binding_next_phone_edittext_tv.getText().toString().trim().equals(binding_next_phone_edittext_again.getText().toString().trim())){
            ToastUtils.showToast("再次确认账户失败，请重新填入。");
            return false;
        }
        bandCard = binding_next_phone_edittext_tv.getText().toString().trim();
        return true;
    }


    private boolean isPhoneNumber(String number){
        Pattern pattern = Pattern.compile(parthen);
        Matcher match = pattern.matcher(number);
        Boolean flag = match.matches();
        return flag;
    }

}
