package com.shkjs.nim.chat.login;

import android.content.Context;

import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.config.Preferences;

/**
 * Created by xiaohu on 2016/8/31.
 */
public class LoginHelper {

    private static AbortableFuture<LoginInfo> loginRequest;

    public static void login(final Context context, final String account, final String token, RequestCallback
            callback) {

        // 登录
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(callback);
    }

    public static void onLoginDone() {
        loginRequest = null;
    }

    public static void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    /**
     * 退出网易云信
     */
    public static void logout() {
        //手动退出
        NIMClient.getService(AuthService.class).logout();
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
        //        ChatRoomHelper.logout();
        AppCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
    }

}
