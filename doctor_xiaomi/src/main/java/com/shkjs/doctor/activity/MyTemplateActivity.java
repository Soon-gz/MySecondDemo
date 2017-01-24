package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTemplateActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_template);
        ButterKnife.bind(this);
        toptitle_tv.setText("我的模板");
    }

    @OnClick({R.id.mytemplates_wenzhen_rv,R.id.mytemplates_bingli_rv,R.id.back_iv})
    public void onMyWalletClick(View view){
        switch (view.getId()){
            case R.id.mytemplates_wenzhen_rv:
                startActivity(new Intent(MyTemplateActivity.this,InquiryTemplateActivity.class).putExtra("template","inquiry"));
                break;
            case R.id.mytemplates_bingli_rv:
                startActivity(new Intent(MyTemplateActivity.this,InquiryTemplateActivity.class).putExtra("template","cases"));
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }
}
