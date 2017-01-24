package com.shkjs.nim.cache;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by xiaohu on 2016/9/8.
 *
 * APP缓存配置，包括当前用户和
 */
public class AppCache {

    private static Context context;

    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        AppCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        AppCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AppCache.context = context.getApplicationContext();
    }
}
