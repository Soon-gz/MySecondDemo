package com.shkjs.doctor.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.orhanobut.logger.Logger;
import com.raspberry.library.http.RestfulRequestBuilder;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.SystemUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.PropertyDto;
import com.shkjs.nim.NIMInitConfig;
import com.shkjs.nim.R;
import com.shkjs.nim.em.ClientType;
import com.shkjs.nim.utils.SoundPlayUtils;
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

    private static Context context;
    private boolean isDebug;
    public static DoctorBean doctorBean;
    public static PropertyDto property;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 应用启动是，需要设置部分在这里处理
     */
    @Override
    public void onCreate() {
        super.onCreate();
        isDebug = AppUtils.isApkInDebug(this);

        context = this;
        //初始化，后期会调整
        Logger.init(getResources().getString(R.string.app_name));
        ToastUtils.init(this);
        SharedPreferencesUtils.init(this);
        SoundPlayUtils.getInstance().init(this);
        initHttp();
        initGuide();
        initinitBugly();
        initNIM();
        initCrashHandler();
    }

    private void initCrashHandler() {
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
            SharedPreferencesUtils.put(Preference.ISFIRSTRUN, true);
            SharedPreferencesUtils.put(Preference.VERSION_CODE, versionCode);
        }
    }

    private void initNIM() {
        NIMInitConfig.Builder builder = new NIMInitConfig.Builder(this);
        NIMInitConfig.init(builder, ClientType.DOCTOR);
    }

    private void initHttp() {

        Logger.e("获取系统当前语言:" + SystemUtils.getLanguage(this));

        Http.DEBUG = true;
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

    public static Context getContext() {
        return context;
    }
}
