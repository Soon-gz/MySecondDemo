package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.nineoldandroids.view.ViewHelper;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.config.UserPreferences;
import com.shkjs.nim.chat.login.LoginHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.impl.UiCallback;

/**
 * Created by LENOVO on 2016/8/12.
 */

public class WelcomeActivity extends AppCompatActivity {

    private ImageView backgroundIV;

    //动画执行时间
    private Long ANIMATION_TIME = 2000L;
    private int startType = -1;
    private final int START_GUIDE = 1;
    private final int START_LOGIN = 2;
    private final int START_AUTO_LOGIN = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        initStartType();
        findView();
        initAnimation();
        //将activity放入自定义管理栈中
        ActivityManager.getInstance().putActivity(this);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initStartType() {
        if (SharedPreferencesUtils.getBoolean(Preference.ISFIRSTRUN)) {
            startType = START_GUIDE;
        } else if (SharedPreferencesUtils.getBoolean(Preference.IS_AUTO_LOGIN)) {
            startType = START_AUTO_LOGIN;
        } else {
            startType = START_LOGIN;
        }
    }

    private void findView() {
        backgroundIV = (ImageView) findViewById(R.id.welcom_background_iv);
    }

    private void initAnimation() {

        ViewHelper.setPivotX(backgroundIV, backgroundIV.getX() / 2);
        ViewHelper.setPivotY(backgroundIV, backgroundIV.getY() / 2);

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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        backgroundIV.setVisibility(View.VISIBLE);
        backgroundIV.startAnimation(animationSet);
    }

    private void login() {
        if (!StringUtil.isEmpty(SharedPreferencesUtils.getString(Preference.USERNAME)) && !StringUtil.isEmpty(SharedPreferencesUtils.getString(Preference.PASSWORD))){
            LoginManager.loginServer(this,SharedPreferencesUtils.getString(Preference.USERNAME),SharedPreferencesUtils.getString(Preference.PASSWORD),true);
        }else {
            startLoginActivity();
        }
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

}
