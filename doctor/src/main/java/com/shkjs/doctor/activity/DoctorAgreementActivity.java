package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DoctorAgreementActivity extends BaseActivity {

    @Bind(R.id.agreement_webview)
    WebView webView;
    @Bind(R.id.toptitle_tv)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_agreement);
        ButterKnife.bind(this);
        textView.setText("用户协议");
        initData();
    }

    @Override
    protected void onDestroy() {
        if (null != webView) {
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (null != webView && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void initData() {
        webView.loadUrl("file:///android_asset/agreement.html");
        webView.setWebViewClient(new WebViewClient());
    }

    @OnClick(R.id.back_iv)
    public void agreementOnClick(){
        finish();
    }
}
