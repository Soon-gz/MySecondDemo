package com.shkjs.patient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.SystemUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.cache.DataCache;

/**
 * Created by LENOVO on 2016/8/12.
 */

public class WelcomeActivity extends BaseActivity {

    private ImageView backgroundIV;
    private TextView explanationTV;

    private Long ANIMATION_TIME = 2000L;//动画执行时间
    private int startType = -1;//闪屏结束后进入哪个页面
    private final int START_GUIDE = 1;//引导页
    private final int START_LOGIN = 2;//登录页
    private final int START_AUTO_LOGIN = 3;//主页

    private static boolean firstEnter = true; // 是否首次进入

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        queryUserInfo = false;

        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }
        //        Intent i_getvalue = getIntent();
        //        String action = i_getvalue.getAction();
        //
        //        if (Intent.ACTION_VIEW.equals(action)) {
        //            Uri uri = i_getvalue.getData();
        //            if (uri != null) {
        //                String name = uri.getQueryParameter("name");
        //                String age = uri.getQueryParameter("age");
        //
        //                Logger.d("name::%s", name);
        //                Logger.d("age::%s", age);
        //            }
        //        }

        initStartType();
        findView();
        if (firstEnter) {
            firstEnter = false;
            initAnimation();
        } else {
            onParseIntent();
        }
    }

    private void initStartType() {
        if (SharedPreferencesUtils.getBoolean(MyApplication.IS_FIRST_RUN)) {
            startType = START_GUIDE;
        } else if (SharedPreferencesUtils.getBoolean(MyApplication.IS_AUTO_LOGIN)) {
            startType = START_AUTO_LOGIN;
        } else {
            startType = START_LOGIN;
        }
    }

    private void findView() {
        backgroundIV = (ImageView) findViewById(R.id.welcom_background_iv);
        explanationTV = (TextView) findViewById(R.id.welcom_explanation_tv);
    }

    private void initAnimation() {

        AnimationSet animationSet = new AnimationSet(true);

        //透明度
        AlphaAnimation alpha = new AlphaAnimation(0.2f, 1f);
        alpha.setDuration(ANIMATION_TIME);
        animationSet.addAnimation(alpha);

        //缩放
        //        ScaleAnimation scale = new ScaleAnimation(0.7f, 1f, 0.7f, 1f);
        //        scale.setDuration(ANIMATION_TIME);
        //        animationSet.addAnimation(scale);

        //保持动画后的位置
        animationSet.setFillAfter(true);

        //动画执行过程监听
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画完成进入相应界面
                onParseIntent();
                //                switch (startType) {
                //                    case START_GUIDE://进入引导页
                //                        startGuideActivity();
                //                        break;
                //                    case START_LOGIN://进入登录页面
                //                        startLoginActivity();
                //                        break;
                //                    case START_AUTO_LOGIN://自动登录
                //                        login();
                //                        break;
                //                    default:
                //                        ToastUtils.showToast(getString(R.string.login_fail));
                //                        startLoginActivity();
                //                        break;
                //                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        backgroundIV.setVisibility(View.VISIBLE);
        backgroundIV.startAnimation(animationSet);
    }

    private void login() {

        final String userName = SharedPreferencesUtils.getString(MyApplication.USER_NAME);
        final String userPwd = SharedPreferencesUtils.getString(MyApplication.USER_PWD);
        Intent intent = getIntent();
        if (null != intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW)) {//微信扫一扫进入
                Uri uri = intent.getData();
                String recode = uri.getQueryParameter(Preference.RECODE);
                if (!TextUtils.isEmpty(recode)) {
                    intent.putExtra(Preference.RECODE, recode);
                    LoginManager.loginServer(this, userName, userPwd, true, intent);
                    return;
                }
            }
        }
        LoginManager.loginServer(this, userName, userPwd, true);
        //        RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
        //            @Override
        //            public void onFailure(Request request, Response response, Exception e) {
        //                super.onFailure(request, response, e);
        //                Logger.e(e.getMessage());
        //                ToastUtils.showToast(getResources().getString(R.string.login_fail));
        //                startLoginActivity();
        //            }
        //
        //            @Override
        //            public void onSuccess(BaseResponse response, int code) {
        //                super.onSuccess(response, code);
        //                if (code == 200) {
        //                    if (response.getStatus().equals(ResponseStatusEnum.SUCCEED.getValue())) {
        //                        SharedPreferencesUtils.put(Preference.USER_NAME, userName);
        //                        SharedPreferencesUtils.put(Preference.USER_PWD, userPwd);
        //                        SharedPreferencesUtils.put(Preference.NIM_PASSWORD, userPwd);
        //                        SharedPreferencesUtils.put(Preference.IS_AUTO_LOGIN, true);//自动登录
        //                        DataCache.getInstance().queryUserInfo();//登录成功，查询用户信息
        //                        startMainActivity();
        //                    } else if (response.getStatus().equals(ResponseStatusEnum.EXCEPTION.getValue())) {
        //                        ToastUtils.showToast(getResources().getString(R.string.login_fail));
        //                        startLoginActivity();
        //                    } else {
        //                        ToastUtils.showToast(getResources().getString(R.string.login_fail) + response
        // .getMsg());
        //                        startLoginActivity();
        //                    }
        //                } else {
        //                    ToastUtils.showToast(getString(R.string.login_fail) + code);
        //                    startLoginActivity();
        //                }
        //            }
        //        };
        //        callback.setContext(this);
        //        callback.setMainThread(true);
        //        callback.setCancelable(false);
        //
        //        HttpProtocol.login(userName, userPwd, callback);
    }

    private void startGuideActivity() {
        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMainActivity(Intent intent) {
        MainActivity.startMainActivity(this, intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onParseIntent();
    }

    private void onParseIntent() {

        // 判断当前app是否正在运行
        if (!SystemUtils.stackResumed(this)) {
            switch (startType) {
                case START_GUIDE://进入引导页
                    startGuideActivity();
                    break;
                case START_LOGIN://进入登录页面
                    startLoginActivity();
                    break;
                case START_AUTO_LOGIN://自动登录
                    login();
                    break;
                default:
                    ToastUtils.showToast(getString(R.string.login_fail));
                    startLoginActivity();
                    break;
            }
        } else {
            if (null == DataCache.getInstance().getUserInfo()) {
                startLoginActivity();
            } else {
                Intent intent = getIntent();
                if (null != intent) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action) && action.equals(Intent.ACTION_VIEW)) {//微信扫一扫进入
                        Uri uri = intent.getData();
                        String recode = uri.getQueryParameter(Preference.RECODE);
                        if (!TextUtils.isEmpty(recode)) {
                            Intent resultIntent = new Intent(this, ResultActivity.class);
                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            resultIntent.putExtra(ResultActivity.class.getSimpleName(), recode);
                            startActivity(resultIntent);
                            finish();
                        } else {
                            startMainActivity(intent);
                        }
                    } else {
                        startMainActivity(intent);
                    }
                }
            }
        }
    }
}
