package com.shkjs.doctor.cache;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.bean.DoctorBean;

/**
 * Created by xiaohu on 2016/10/10.
 */

public class DataCache {

    private static DataCache dataCache;
    //当前登录用户信息
    private DoctorBean userInfo;
    //是否已登录网易服务器
    private boolean isLoginNim = false;

    private DataCache() {
    }

    public static DataCache getInstance() {
        if (null == dataCache) {
            synchronized (DataCache.class) {
                if (null == dataCache) {
                    dataCache = new DataCache();
                }
            }
        }
        return dataCache;
    }

    /**
     * 得到当前登录用户信息
     *
     * @return
     */
    public DoctorBean getUserInfo() {
        return userInfo;
    }

    /**
     * 更新当前登录用户信息
     *
     * @param userInfo
     */
    public void setUserInfo(DoctorBean userInfo) {
        this.userInfo = userInfo;
    }



    /**
     * 是否登录网易服务器
     *
     * @return
     */
    public boolean isLoginNim() {
        isLoginNim = true;
        StatusCode statusCode = NIMClient.getStatus();
        if (!statusCode.equals(StatusCode.LOGINED)) {
            isLoginNim = false;
        }
        return isLoginNim;
    }

    /**
     * 更新网易登录状态
     *
     * @param loginNim
     */
    public void setLoginNim(boolean loginNim) {
        isLoginNim = loginNim;
    }



    public void cleanCache() {
        userInfo = null;
        isLoginNim = false;
    }
}
