package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFrientsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.add_friends_phone_edittext_tv)
    EditText add_friends_phone_edittext_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_frients);
        ButterKnife.bind(this);
        toptitle_tv.setText("添加医生好友");
    }

    public boolean checkIsempty(){
        if (StringUtil.isEmpty(add_friends_phone_edittext_tv.getText().toString().trim())){
            ToastUtils.showToast("请填写手机号");
            return false;
        }
        return true;
    }

    @OnClick({R.id.back_iv,R.id.add_frients_final_tv})
    public void addmyfrientsOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.add_frients_final_tv:
                if (checkIsempty()){
                    ToastUtils.showToast("添加好友成功");
                    finish();
                }
                break;
        }
    }
}
