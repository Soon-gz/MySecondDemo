package com.shkjs.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.doctor.activity.LoginActivity;
import com.shkjs.doctor.activity.MainActivity;
import com.shkjs.doctor.activity.PersonalMessageActivity;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.PropertyDto;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.DoctorClient;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.config.UserPreferences;
import com.shkjs.nim.chat.login.LoginHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by xiaohu on 2016/11/7.
 * <p>
 * 登录成功后，获取相应数据和进行其他三方服务登录
 */

public class LoginManager {

    /**
     * 登录服务器
     *
     * @param context  activity
     * @param userName 用户名
     * @param userPwd  用户密码
     */
    public static void loginServer(final Activity context, final String userName, final String userPwd, final boolean
            isFinish) {
        RaspberryCallback<ObjectResponse<DoctorClient>> callback = new
                RaspberryCallback<ObjectResponse<DoctorClient>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                if (isFinish) {
                    startLoginActivity(context, isFinish);
                }
            }

            @Override
            public void onSuccess(ObjectResponse<DoctorClient> response, int code) {
                super.onSuccess(response, code);
                if (code == 404 && isFinish) {
                    context.finish();
                }
                if (HttpProtocol.checkStatus(response, code)) {
                    SharedPreferencesUtils.put(Preference.IS_AUTO_LOGIN, true);//自动登录
                    SharedPreferencesUtils.put(Preference.USERNAME, userName);
                    SharedPreferencesUtils.put(Preference.PASSWORD, userPwd);
                    initGetui(context);//上传个推clientID
                    CrashReport.setUserId(userName);
                    if (!DataCache.getInstance().isLoginNim()) {
                        loginNim(context, null);//登录网易服务器
                    }
                    loadDoctorData(context, isFinish);
                    queryProperty(context);
                } else {
                    startLoginActivity(context, isFinish);
                }
            }
        };
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);
        HttpProtocol.login(userName, MD5Utils.encodeMD5(userPwd), callback);
    }

    /**
     * 第二步，登录网易注册账号
     */
    public static void loginNim(Activity context, final LognimCallback lognimCallback) {
        final String userName = SharedPreferencesUtils.getString(Preference.USERNAME) + Preference.NIM_DOCTOR_NAME;
        final String userPwd = SharedPreferencesUtils.getString(Preference.USERNAME) + Preference.NIM_DOCTOR_NAME +
                Preference.NIM_PASS_WORD;
        LoginHelper.login(context.getApplicationContext(), userName, MD5Utils.encodeMD5(userPwd), new
                RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                if (lognimCallback != null) {
                    lognimCallback.logNimCallback();
                }
                LoginHelper.onLoginDone();
                AppCache.setAccount(userName);
                LoginHelper.saveLoginInfo(userName, MD5Utils.encodeMD5(userPwd));

                // 初始化消息提醒
                //                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
                NIMClient.toggleNotification(false);
                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(AppCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
                DataCacheManager.buildDataCacheAsync();
            }

            @Override
            public void onFailed(int code) {
                LoginHelper.onLoginDone();
                if (code == 302 || code == 404) {
                    //                    ToastUtils.showToast("登录失败！");
                    Log.i("tag00", "第二步登录失败。");
                }
            }

            @Override
            public void onException(Throwable exception) {
                Logger.e(exception.getMessage());
                LoginHelper.onLoginDone();
            }
        });
    }

    public interface LognimCallback {
        void logNimCallback();
    }

    /**
     * 个推初始化
     *
     * @param context activity
     */
    public static void initGetui(Activity context) {
        PushManager.getInstance().initialize(context.getApplicationContext());
        //获取一次clientID，以便上传至服务器（receiver中处理）
        //        Logger.e("client: %s ", PushManager.getInstance().getClientid(context.getApplicationContext()));
        //        Logger.e("sdk status: %s ", PushManager.getInstance().isPushTurnedOn(context.getApplicationContext
        // ()));
    }

    /**
     * 查询用户信息
     */
    /**
     * 第三步加载医生资料，用于判断是否已认证
     */
    private static void loadDoctorData(final Activity context, final boolean isFinish) {
        //主页获取医生详情 未测试  2016/10/8
        RaspberryCallback<ObjectResponse<DoctorBean>> callback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() == null) {
                        if (isFinish) {
                            context.finish();
                            Intent intent = new Intent(context, PersonalMessageActivity.class);
                            intent.putExtra("certificate", Preference.UNCERTIFICATION);
                            intent.putExtra(Preference.IS_FROM_WELCOME, "yes");
                            context.startActivity(intent);
                        } else {
                            context.startActivity(new Intent(context, PersonalMessageActivity.class).putExtra
                                    ("certificate", Preference.UNCERTIFICATION));
                        }
                    } else {
                        DataCache.getInstance().setUserInfo(response.getData());
                        MyApplication.doctorBean = response.getData();
                        startMainActivity(response.getData(), context);
                    }
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setShowDialog(false);
        HttpProtocol.detail(callback);
    }

    /**
     * 查询配置信息
     *
     * @param context Context
     */
    public static void queryProperty(Context context) {

        RaspberryCallback<ObjectResponse<PropertyDto>> callback = new RaspberryCallback<ObjectResponse<PropertyDto>>() {
            @Override
            public void onSuccess(ObjectResponse<PropertyDto> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null) {
                        MyApplication.property = response.getData();
                    }
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(context);
        callback.setMainThread(false);
        callback.setShowDialog(false);
        HttpProtocol.queryProperty(callback);
    }

    /**
     * 进入主界面
     */
    private static void startMainActivity(DoctorBean doctorBean, Activity context) {
        MainActivity.startMainActivity(context, new Intent(context, MainActivity.class).putExtra("doctorbean",
                doctorBean));
        context.finish();
    }

    /**
     * 进入登录界面
     *
     * @param context
     */
    private static void startLoginActivity(Activity context, boolean isFinish) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        if (isFinish) {
            context.finish();
        }
    }

}
