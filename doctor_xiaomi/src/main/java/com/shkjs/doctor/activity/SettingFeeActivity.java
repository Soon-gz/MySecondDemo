package com.shkjs.doctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.DoctorFeeBean;
import com.shkjs.doctor.data.DoctorTag;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.util.BalanceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFeeActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.seting_fee_picture)
    TextView seting_fee_picture;
    @Bind(R.id.seting_fee_video)
    TextView seting_fee_video;

    private long view_video_fee;
    private  long picture_fee;
    private DoctorTag doctorTag;
    private RaspberryCallback<ObjectResponse<DoctorFeeBean>>callback;
    private String paltLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_fee);
        ButterKnife.bind(this);
        view_video_fee = getIntent().getIntExtra(Preference.VIEW_VIDEO_FEE,0);
        picture_fee = getIntent().getIntExtra(Preference.PICTURE_FEE,0);
        paltLevel = getIntent().getStringExtra(Preference.AUTHENTATIC);
        doctorTag = DoctorTag.valueOf(getIntent().getStringExtra(Preference.DOCTORTAG));
        initListener();
        toptitle_tv.setText("诊金设置");
    }

    private void initListener() {
        callback = new RaspberryCallback<ObjectResponse<DoctorFeeBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorFeeBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)){
                    if (Preference.CERTIFICATION.equals(paltLevel)){
                        initView(picture_fee - response.getData().getCertifictionTxt(),view_video_fee - response.getData().getCertifictionVideo());
                    }else if (Preference.AUTHORITY.equals(paltLevel)){
                        initView(picture_fee - response.getData().getAuthorityTxt(),view_video_fee - response.getData().getAuthorityVideo());
                    }else {
                        initView(0,0);
                    }
                }
            }
        };
        AudioHelper.initCallBack(callback,this,true);
        if (doctorTag.equals(DoctorTag.NORMAL)){
            HttpProtocol.getMoneyDelay(callback);
        }else {
            initView(picture_fee,view_video_fee);
        }
    }

    private void initView(long pictureFee,long videoFee) {
        if (pictureFee > 0 && videoFee > 0){
            seting_fee_video.setText(BalanceUtils.formatBalance2(videoFee));
            seting_fee_picture.setText(BalanceUtils.formatBalance2(pictureFee));
        }else {
            seting_fee_video.setText("0.00");
            seting_fee_picture.setText("0.00");
        }
    }

    @OnClick({R.id.back_iv,R.id.custor_phone})
    public void settingFeeOnClick(View view){
        switch (view.getId()){
            case R.id.custor_phone:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:"+"4008859120"));
                startActivity(intent);
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }
}
