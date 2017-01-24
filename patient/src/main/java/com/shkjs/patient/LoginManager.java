package com.shkjs.patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import com.igexin.sdk.PushManager;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.config.UserPreferences;
import com.shkjs.nim.chat.login.LoginHelper;
import com.shkjs.patient.activity.LoginActivity;
import com.shkjs.patient.activity.MainActivity;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.bean.PatientClient;
import com.shkjs.patient.bean.UserInfo;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
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
     * @param userName 用户名
     * @param userPwd  用户密码
     */
    public static void loginServer(final String userName, final String userPwd) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
            return;
        }
        if (DataCache.getInstance().isLogining()) {
            return;
        }
        RaspberryCallback<ObjectResponse<PatientClient>> callback = new
                RaspberryCallback<ObjectResponse<PatientClient>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                DataCache.getInstance().setLogining(false);
            }

            @Override
            public void onSuccess(ObjectResponse<PatientClient> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        queryUserInfo(null);
                    }
                }
                DataCache.getInstance().setLogining(false);
            }
        };
        callback.setMainThread(false);
        HttpProtocol.login(userName, MD5Utils.encodeMD5(userPwd), callback);
        DataCache.getInstance().setLogining(true);
    }

    /**
     * 登录服务器
     *
     * @param context  activity
     * @param userName 用户名
     * @param userPwd  用户密码
     */
    public static void loginServer(final Activity context, final String userName, final String userPwd, final boolean
            isFinish) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
            startLoginActivity(context, isFinish);
            return;
        }
        RaspberryCallback<ObjectResponse<PatientClient>> callback = new
                RaspberryCallback<ObjectResponse<PatientClient>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast2(context.getResources().getString(R.string.login_fail) + ",请检查网络", Gravity.CENTER);
                startLoginActivity(context, isFinish);
            }

            @Override
            public void onSuccess(ObjectResponse<PatientClient> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        SharedPreferencesUtils.put(MyApplication.USER_NAME, userName);
                        SharedPreferencesUtils.put(MyApplication.USER_PWD, userPwd);
                        SharedPreferencesUtils.put(MyApplication.IS_AUTO_LOGIN, true);//自动登录
                        queryUserInfo(null);//登录成功，查询用户信息
                        initGetui(context);//上传个推clientID
                        CrashReport.setUserId(userName);
                        loginNim(context);//登录网易服务器
                        startMainActivity(context);
                        context.finish();
                    } else {
                        ToastUtils.showToast2(response.getMsg(), Gravity.CENTER);
                        startLoginActivity(context, isFinish);
                    }
                } else {
                    ToastUtils.showToast2(context.getString(R.string.login_fail) + code, Gravity.CENTER);
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
     * 登录服务器
     *
     * @param context  activity
     * @param userName 用户名
     * @param userPwd  用户密码
     */
    public static void loginServer(final Activity context, final String userName, final String userPwd, final boolean
            isFinish, final Intent intent) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
            startLoginActivity(context, isFinish);
            return;
        }
        RaspberryCallback<ObjectResponse<PatientClient>> callback = new
                RaspberryCallback<ObjectResponse<PatientClient>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast2(context.getResources().getString(R.string.login_fail) + ",请检查网络", Gravity.CENTER);
                startLoginActivity(context, isFinish);
            }

            @Override
            public void onSuccess(ObjectResponse<PatientClient> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        SharedPreferencesUtils.put(MyApplication.USER_NAME, userName);
                        SharedPreferencesUtils.put(MyApplication.USER_PWD, userPwd);
                        SharedPreferencesUtils.put(MyApplication.IS_AUTO_LOGIN, true);//自动登录
                        queryUserInfo(null);//登录成功，查询用户信息
                        initGetui(context);//上传个推clientID
                        CrashReport.setUserId(userName);
                        loginNim(context);//登录网易服务器
                        startMainActivity(context, intent);
                        context.finish();
                    } else {
                        ToastUtils.showToast2(response.getMsg(), Gravity.CENTER);
                        startLoginActivity(context, isFinish);
                    }
                } else {
                    ToastUtils.showToast2(context.getString(R.string.login_fail) + code, Gravity.CENTER);
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
     * 登录网易服务器
     *
     * @param context Context
     */
    public static void loginNim(Context context) {
        loginNim(context, null);
    }

    /**
     * 登录网易服务器
     *
     * @param context  Context
     * @param listener listener
     */
    public static void loginNim(final Context context, final UserInfoListener listener) {
        final String userName = SharedPreferencesUtils.getString(MyApplication.USER_NAME) + MyApplication.NIM_USER_NAME;
        final String userPwd = SharedPreferencesUtils.getString(MyApplication.USER_NAME) + MyApplication.NIM_USER_NAME +
                MyApplication.NIM_PASS_WORD;
        LoginHelper.login(context, userName, MD5Utils.encodeMD52(userPwd), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                if (null != listener) {
                    listener.onFinish();
                }
                Logger.d("login nim success");
                LoginHelper.onLoginDone();
                AppCache.setAccount(userName);
                LoginHelper.saveLoginInfo(userName, MD5Utils.encodeMD52(userPwd));

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
                    Logger.e("用户名或密码错误");
                }
            }

            @Override
            public void onException(Throwable exception) {
                Logger.e(exception.getMessage());
                LoginHelper.onLoginDone();
            }
        });
    }

    /**
     * 个推初始化
     *
     * @param context activity
     */
    public static void initGetui(Activity context) {
        PushManager.getInstance().initialize(context.getApplicationContext());
        //获取一次clientID，以便上传至服务器（receiver中处理）
        Logger.e("client: %s ", PushManager.getInstance().getClientid(context.getApplicationContext()));
        Logger.e("sdk status: %s ", PushManager.getInstance().isPushTurnedOn(context.getApplicationContext()));
    }

    /**
     * 查询用户信息
     */
    public static void queryUserInfo(final UserInfoListener listener) {
        //缓存到有用户信息，则不查询
        if (null != DataCache.getInstance().getUserInfo() && null != DataCache.getInstance().getUserInfo().getId()) {
            return;
        }

        RaspberryCallback<ObjectResponse<UserInfo>> callback = new RaspberryCallback<ObjectResponse<UserInfo>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onSuccess(ObjectResponse<UserInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        DataCache.getInstance().setUserInfo(response.getData());
                        MyApplication.getInstance().sendBroadcast(new Intent(Preference.UPDATE_VIEW_ACTION));
                        if (null != listener) {
                            listener.onFinish();
                        }
                    }
                }
            }
        };
        callback.setMainThread(false);
        HttpProtocol.getUserDetail(null, callback);
    }

    /**
     * 进入主界面
     *
     * @param context
     */
    private static void startMainActivity(Context context) {
        MainActivity.startMainActivity(context);
    }

    /**
     * 进入主界面
     *
     * @param context
     * @param intent
     */
    private static void startMainActivity(Context context, Intent intent) {
        MainActivity.startMainActivity(context, intent);
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

    public interface UserInfoListener {
        void onFinish();
    }
}
