package com.shkjs.patient.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.qrcode.QRCodeUtil;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.DownloadAddress;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.data.em.PhoneTerminalType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p>
 * 关于
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.about_android_patient_qrcode_iv)
    ImageView patientIV;
    @Bind(R.id.about_android_doctor_qrcode_iv)
    ImageView doctorIV;
    @Bind(R.id.edition_code_tv)
    TextView codeTV;

    private Toolbar toolbar;

    private static final String defaultUrl = HttpBase.APP_DOWNLOAD_URL;
    private static final String HTTP = "http://";
    private String doctorUrl = "https://server.120yxh.com:8443/adminWeb/index/downloadDoctor";
    private String patientUrl = "https://server.120yxh.com:8443/adminWeb/index/downloadPatient";

    private static final int UPDATE_VIEW = 121;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_VIEW) {
                doctorUrl = TextUtils.isEmpty(doctorUrl) ? defaultUrl : (HTTP + doctorUrl);
                patientUrl = TextUtils.isEmpty(patientUrl) ? defaultUrl : (HTTP + patientUrl);
                doctorIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(doctorUrl));
                patientIV.setImageBitmap(QRCodeUtil.CreateTwoDCode(patientUrl));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.about_text);

        initData();
        initListener();
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

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        for (DownloadAddress address : response.getData()) {
                            if (address.getType().equals(PhoneTerminalType.ANDROID_DOCTOR)) {
                                doctorUrl = address.getAddress();
                            } else if (address.getType().equals(PhoneTerminalType.ANDROID_USER)) {
                                patientUrl = address.getAddress();
                            }
                        }
                    }
                    handler.sendEmptyMessage(UPDATE_VIEW);
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(false);
        Page page = new Page();
        page.setPageSize(Integer.MAX_VALUE);
        HttpProtocol.queryDownloadUrls(page, callback);
    }
}
