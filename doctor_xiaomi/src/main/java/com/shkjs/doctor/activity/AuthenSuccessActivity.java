package com.shkjs.doctor.activity;

import android.os.Bundle;

import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authen_success);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.certification_success_btn)
    public void click(){
        MainActivity.startMainActivity(this);
        finish();
    }
}
