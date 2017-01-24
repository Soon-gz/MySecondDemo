package com.shkjs.nim.chat.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.shkjs.nim.R;
import com.shkjs.nim.cache.AppCache;

/**
 * Created by xiaohu on 2016/9/8.
 * <p/>
 * 网易IM账户相关配置
 */
public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        return AppCache.getContext().getSharedPreferences(AppCache.getContext().getString(R.string.app_name), Context
                .MODE_PRIVATE);
    }
}

