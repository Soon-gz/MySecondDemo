package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.util.ToggleButton;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.nim.chat.login.LoginHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                           // 标题
    @Bind(R.id.settings_newmsg_toggle_btn)
    ToggleButton settings_newmsg_toggle_btn;           //消息开关



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //注解
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("设置");
        //设置监听事件
        initListener();
    }

    private void initListener() {
        settings_newmsg_toggle_btn.setToggleOn(true);
        settings_newmsg_toggle_btn.setToggleOff(true);
        if ( SharedPreferencesUtils.getBoolean(Preference.PUSH_MSG)){
            settings_newmsg_toggle_btn.toggleOn();
        }else {
            settings_newmsg_toggle_btn.toggleOff();
            PushManager.getInstance().turnOffPush(getApplicationContext());
        }
        settings_newmsg_toggle_btn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    PushManager.getInstance().turnOnPush(getApplicationContext());
                    SharedPreferencesUtils.put(Preference.PUSH_MSG,true);
                    ToastUtils.showToast("已打开通知栏消息通知。");
                }else {
                    PushManager.getInstance().turnOffPush(getApplicationContext());
                    SharedPreferencesUtils.put(Preference.PUSH_MSG,false);
                    ToastUtils.showToast("已关闭通知栏消息通知。");
                }
            }
        });
    }

    /**
     * 所有的点击事件
     * @param view
     */
    @OnClick({R.id.back_iv,R.id.settings_changepsd_rv,R.id.settings_outlog_rv})
    public void settingsOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.settings_changepsd_rv:
                startActivity(new Intent(SettingsActivity.this,ChangePWActivity.class));
                break;
            case R.id.settings_outlog_rv:
                CustomAlertDialog.dialogExSureCancel("确定退出登录吗？", this, new CustomAlertDialog.OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        logout();
                    }
                });
                break;
        }
    }

    private void logout() {
        RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {

            }

            @Override
            public void onSuccess(BaseResponse response, int code) {

            }
        };

        HttpProtocol.logout(callback);
        LoginHelper.logout();
        SharedPreferencesUtils.put(Preference.IS_AUTO_LOGIN, false);//取消自动登录
        DataCache.getInstance().cleanCache();//清除缓存数据、状态

        ActivityManager.getInstance().finishActivity();
        ActivityManager.getInstance().finishAllActivity();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
