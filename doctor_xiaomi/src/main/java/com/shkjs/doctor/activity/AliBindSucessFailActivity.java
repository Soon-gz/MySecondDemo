package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.util.ActivityManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AliBindSucessFailActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    private String SUCCESSED_FAIL;
    @Bind(R.id.binding_back_retry_tv)
    TextView binding_back_retry_tv;
    @Bind(R.id.binding_back_tv)
    TextView binding_back_tv;
    @Bind(R.id.binding_successfail_msg_tv)
    TextView binding_successfail_msg_tv;
    @Bind(R.id.binding_success_fail_tv)
    TextView binding_success_fail_tv;
    @Bind(R.id.binding_success_fail_iv)
    CircleImageView binding_success_fail_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_bind_sucess_fail);
        ButterKnife.bind(this);
        toptitle_tv.setText(getResources().getString(R.string.alipay_title));

        if (getIntent() != null) {
            SUCCESSED_FAIL = getIntent().getStringExtra(Preference.SUCCESSED_FAIL);
        }
        if (SUCCESSED_FAIL != null) {
            checkSuccessFail(SUCCESSED_FAIL);
        }
    }

    private void checkSuccessFail(String successed_fail) {
        switch (successed_fail) {
            case Preference.SUCCESSED:
                binding_success_fail_iv.setImageResource(R.drawable.main_common_success);
                binding_back_tv.setVisibility(View.GONE);
                binding_back_retry_tv.setText(getResources().getString(R.string.back));
                binding_success_fail_tv.setText(R.string.alipay_success);
                binding_successfail_msg_tv.setText(getResources().getString(R.string.alipay_success_msg));
                binding_back_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                break;
            case Preference.FAIL:
                binding_success_fail_iv.setImageResource(R.drawable.main_common_failure);
                binding_back_tv.setVisibility(View.VISIBLE);
                binding_back_retry_tv.setText(getResources().getString(R.string.alipay_retry));
                binding_success_fail_tv.setText(getResources().getString(R.string.alipay_fail));
                binding_successfail_msg_tv.setText(getResources().getString(R.string.alipay_fail_msg));
                binding_back_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        ActivityManager.getInstance().finishActivity(BindingAlipayNextActivity.class);
                    }
                });
                break;
        }
    }

    @OnClick({R.id.binding_back_retry_tv,R.id.back_iv})
    public void alipayOnClick(View view) {
        switch (view.getId()){
            case R.id.binding_back_retry_tv:
                finish();
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }


}
