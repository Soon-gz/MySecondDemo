package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InquiryTemplateActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.inquiry_template_iv)
    ImageView inquiry_template_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_template);
        ButterKnife.bind(this);
        String template = getIntent().getStringExtra("template");
        if ("inquiry".equals(template)){
            toptitle_tv.setText("问诊表模板");
            inquiry_template_iv.setImageResource(R.drawable.inquiry);
        }else {
            toptitle_tv.setText("病历表模板");
            inquiry_template_iv.setImageResource(R.drawable.cases);
        }
    }

    @OnClick(R.id.back_iv)
    public void inquiryTemplateOnClick(View view) {
        finish();
    }
}
