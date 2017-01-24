package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.qrcode.QRCodeUtil;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.DownloadAddress;
import com.shkjs.doctor.bean.Page;
import com.shkjs.doctor.data.PhoneTerminalType;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListPageResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.aboutus_android_patient_download_iv)
    ImageView patientIV;
    @Bind(R.id.aboutus_android_doctor_download_iv)
    ImageView doctorIV;
    @Bind(R.id.edition_code_tv)
    TextView codeTV;

    private static final String defaultUrl = HttpBase.BASE_URL + "index/download";
    private static final String HTTP = "http://";
    private String doctorUrl = "https://server.120yxh.com:8443/adminWeb/index/downloadDoctor";
    private String patientUrl = "https://server.120yxh.com:8443/adminWeb/index/downloadPatient";

    private static final int UPDATE_VIEW = 121;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_VIEW) {
                patientUrl = TextUtils.isEmpty(patientUrl) ? defaultUrl : (HTTP + patientUrl);
                doctorUrl = TextUtils.isEmpty(doctorUrl) ? defaultUrl : (HTTP + doctorUrl);
                doctorIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(doctorUrl));
                patientIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(patientUrl));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        toptitle_tv.setText("关于");
        initData();
        //        queryDownloadUrls();
    }

    private void initData() {
        //        String str = "功能完善中，敬请期待...";
        codeTV.setText(AppUtils.getVersionName(this));
        //        doctorIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(defaultUrl));
        //        patientIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(defaultUrl));
        doctorIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(doctorUrl));
        patientIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(patientUrl));
    }

    /**
     * 查询下载地址
     */
    private void queryDownloadUrls() {

        RaspberryCallback<ListPageResponse<DownloadAddress>> callback = new
                RaspberryCallback<ListPageResponse<DownloadAddress>>() {

            @Override
            public void onSuccess(ListPageResponse<DownloadAddress> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (null != response.getData()) {
                        for (DownloadAddress address : response.getData()) {
                            if (address.getType().equals(PhoneTerminalType.ANDROID_USER)) {
                                patientUrl = address.getAddress();
                            } else if (address.getType().equals(PhoneTerminalType.ANDROID_DOCTOR)) {
                                doctorUrl = address.getAddress();
                            }
                        }
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(false);
        Page page = new Page();
        page.setPageSize(Integer.MAX_VALUE);
        HttpProtocol.queryDownloadUrls(page, callback);
    }

    @OnClick(R.id.back_iv)
    public void aboutUsOnClick(View view) {
        finish();
    }
}
