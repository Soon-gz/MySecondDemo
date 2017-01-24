package com.shkjs.patient.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.raspberry.library.http.RestfulRequestBuilder;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.SystemUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.NIMInitConfig;
import com.shkjs.nim.em.ClientType;
import com.shkjs.nim.utils.SoundPlayUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.tencent.bugly.crashreport.CrashReport;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.impl.RequestCallBuilder;

import java.util.concurrent.TimeUnit;


/**
 * Created by xiaohu on 2016/8/12.
 * <p/>
 * 初始化相关设置、数据
 */

public class MyApplication extends Application {

    /**
     * 用户名
     */
    public static final String USER_NAME = "userName";
    /**
     * 用户密码
     */
    public static final String USER_PWD = "password";
    /**
     * 用户NIM用户名后缀
     */
    public static final String NIM_USER_NAME = "_user";
    /**
     * 用户NIM用户名后缀
     */
    public static final String NIM_DOCTOR_NAME = "_doctor";
    /**
     * 用户NIM密码后缀
     */
    public static final String NIM_PASS_WORD = "123456";
    /**
     * 用户NIM密码
     */
    public static final String NIM_PASSWORD = "nim_password";
    /**
     * 是否首次运行
     */
    public static final String IS_FIRST_RUN = "isFirstRun";
    /**
     * 是否自动登录
     */
    public static final String IS_AUTO_LOGIN = "isAutoLogin";
    /**
     * 声音提醒设置
     */
    public static final String VOICE_SETTING = "voice_setting";

    private static MyApplication application;
    private boolean isDebug;


    public static MyApplication getInstance() {
        return application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    /**
     * 应用启动是，需要设置部分在这里处理
     */
    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        isDebug = AppUtils.isApkInDebug(this);

        //初始化，debug和release配置不同
        initUtils();
        initinitBugly();
        initHttp();
        initNIM();
        initGuide();
        initCrash();

    }

    private void initCrash() {
        //        CrashHandler.getInstance().setCustomCrashHanler(this, new CrashHandler.ExceptionHandlerListener() {
        //            @Override
        //            public void handler() {
        //                MainActivity.startMainActivity(context);
        //                Logger.d("程序出现异常了哦");
        //            }
        //        });
    }

    private void initUtils() {
        if (isDebug) {
            Logger.init(getResources().getString(R.string.app_name));
        } else {
            Logger.init(getResources().getString(R.string.app_name)).logLevel(LogLevel.NONE);
        }
        ToastUtils.init(this);
        SharedPreferencesUtils.init(this);
        SoundPlayUtils.getInstance().init(this);
    }

    private void initinitBugly() {
        /**
         * 上下文，注册时申请的APPID，是否为调试模式
         */
        CrashReport.initCrashReport(getApplicationContext(), Preference.BUGLY_APPID, isDebug);
    }

    private void initGuide() {
        int versionCode = AppUtils.getVersionCode(this);
        if (versionCode > SharedPreferencesUtils.getInt(Preference.VERSION_CODE)) {
            SharedPreferencesUtils.put(MyApplication.IS_FIRST_RUN, true);
            SharedPreferencesUtils.put(Preference.VERSION_CODE, versionCode);
        }
    }

    private void initNIM() {
        NIMInitConfig.Builder builder = new NIMInitConfig.Builder(this);
        NIMInitConfig.init(builder, ClientType.PATIENT);
    }

    private void initHttp() {

        Logger.e("获取系统当前语言:" + SystemUtils.getLanguage(this));

        Http.DEBUG = isDebug;
        RestfulRequestBuilder restfulRequestBuilder = new RestfulRequestBuilder();
        restfulRequestBuilder.setBuilderListener(new RequestCallBuilder.BuilderListener() {
            @Override
            public void onCreateBuilder(Request.Builder builder) {
                // 请求头构建,在这里你可以做一些请求头的初始化操作
                builder.addHeader("X-Requested-With", "XMLHttpRequest");
                //                builder.addHeader("Accept-Lanuage", SystemUtils.getLanguage(getApplicationContext()));
            }

            @Override
            public boolean onBuildGetParams(StringBuilder sb, boolean isFirst) {
                // 构建Get时调用
                // isFirst 用于告知是否是第一个参数
                // 因为GET请求第一个参数是加上 "?" ,而其后参数则是加上 "&"
                // 返回时也用于告知当前参数中是否已经有参数了,
                // 如果已经有了哪么返回 false ,没有加上任何参数 返回 true
                return true;
            }

            @Override
            public void onBuildFormBody(FormEncodingBuilder formEncodingBuilder) {
                // 构建 Form body 时调用
            }

            @Override
            public void onBuildMultipartBody(MultipartBuilder multipartBuilder) {
                // 构建 Multipart body 时调用
            }
        });
        // 设置http builder
        Http.getInstance().setRequestBuilder(restfulRequestBuilder);
        //保存cookie
        Http.getInstance().enableSaveCookie(getApplicationContext());
        //超时时间
        Http.getInstance().interceptToProgressResponse().setConnectTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        Http.getInstance().interceptToProgressResponse().setReadTimeout(20 * 1000, TimeUnit.MILLISECONDS);

    }
}
