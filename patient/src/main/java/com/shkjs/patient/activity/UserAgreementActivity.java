package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/30.
 */

public class UserAgreementActivity extends BaseActivity {

    @Bind(R.id.agreement_webview)
    WebView webView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_agreement);

        //绑定控件
        ButterKnife.bind(this);
        Log.i("tag00","UserAgreementActivity onCreate");

        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.user_agreement);
        queryUserInfo = false;

        initData();
        initListener();

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

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
